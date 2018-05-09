package org.randywebb.wlts.util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

/** Makes generating .kml files a bit easier.
    Note: You still have to know how to format a .kml file, this class just mirrors the XML.
*/
public final class KMLWriter {

    /** default constructor. */
    private KMLWriter() {
    }

    // Can be used for logging debugging messages.
    //private static Logger log = LoggerFactory.getLogger(KMLWriter.class);

    /** Any element that needs to be written to the file. */
    public interface Item {
        /** Writes the element to the output.
            @param prefix The indentation level
            @param output The stream to write the element to
            @throws IOException on io errors
        */
        void write(String prefix, FileWriter output) throws IOException;
    };

    /** A simple tag. It just has &lt;tag&gt;value&lt;tag&gt; */
    public static class Simple implements Item {

        /**
            @param tagType The tag type
            @param tagValue the value for the tag
        */
        public Simple(String tagType, String tagValue) {
            tag = tagType;
            value = tagValue;
        }

        /** Writes the tag to the output.
            @param prefix The indentation level
            @param output The stream to write the element to
            @throws IOException on io errors
        */
        public void write(String prefix, FileWriter output) throws IOException {
            output.append(prefix + "<" + tag + "><![CDATA[" + value + "]]></" + tag + ">\n");
        }

        /** The type of tag. */
        private String tag;
        /** The value for the tag. */
        private String value;

    }

    /** Name element. */
    public static class Name extends Simple {
        /** @param name The name to add */
        public Name(String name) {
            super("name", name);
        }
    }

    /** Description element. */
    public static class Description extends Simple {
        /** @param description The description to add */
        public Description(String description) {
            super("description", description);
        }
    }

    /** Mark the current object invisible. */
    public static class Invisible extends Simple {
        /** visibility defaults to 1, so to make invisible, add a visibility=0 tag. */
        public Invisible() {
            super("visibility", "0");
        }
    }

    /** Use a style URL. */
    public static class UseStyle extends Simple {
        /** @param name the name of the style (will use anchor url, ie #name) */
        public UseStyle(String name) {
            super("styleUrl", "#" + name);
        }
    }

    /** General category for items that have altitude mode. */
    public abstract static class TerrainElement implements Item {

        /** A line that does not tessellate and has no altitude mode and no coordinates. */
        public TerrainElement() {
            altitudeMode = null;
        }

        /** Sets the altitude mode.
            Preferred to use altitudeRelativeToGround() or altitudeAbsolute().
            @param mode The mode name to set
            @return this for call chaining.
        */
        public TerrainElement altitudeMode(String mode) {
            altitudeMode = mode;
            return this;
        }

        /** Sets the relative to ground altitude mode.
            @return this for call chaining.
        */
        public TerrainElement altitudeRelativeToGround() {
            return altitudeMode("relativeToGround");
        }

        /** Sets the relative to sea floor altitude mode.
            @return this for call chaining.
        */
        public TerrainElement altitudeRelativeToSeaFloor() {
            return altitudeMode("relativeToSeaFloor");
        }

        /** Sets the absolute altitude mode.
            @return this for call chaining.
        */
        public TerrainElement altitudeAbsolute() {
            return altitudeMode("absolute");
        }

        /** Writes out the altitudeMode.
            Coordinates are written out in the order they were added, in long, lat, alt groups.
            @param prefix The indentation
            @param output The stream to write to
            @throws IOException on io error
        */
        public void write(String prefix, FileWriter output) throws IOException {
            if (null != altitudeMode) {
                output.append(prefix + "\t<altitudeMode>" + altitudeMode + "</altitudeMode>\n");
            }
        }

        /** What altitude mode should we use (or null for do not specify). */
        private String altitudeMode;
    }

    /** Add a LineString to the map. */
    public static class Line extends TerrainElement {

        /** A line that does not tessellate and has no altitude mode and no coordinates. */
        public Line() {
            tessellate = false;
            coordinates = new ArrayList<String>();
        }

        /**
            @param t Set tessellate to this value.
            @return this for call chaining.
        */
        public Line tessellate(boolean t) {
            tessellate = t;
            return this;
        }

        /**
            @param latitude latitude of coordinate to add to the list
            @param longitude longitude of the coordinate to add to the list
            @param altitude altitude of the coordinate to add to the list
            @return this for call chaining.
        */
        public Line add(double latitude, double longitude, double altitude) {
            coordinates.add("" + longitude + "," + latitude + "," + altitude);
            return this;
        }

        /** Writes out the LineString.
            Coordinates are written out in the order they were added, in long, lat, alt groups.
            @param prefix The indentation
            @param output The stream to write to
            @throws IOException on io error
        */
        @Override
        public void write(String prefix, FileWriter output) throws IOException {
            String lineEnding = "";

            output.append(prefix + "<LineString>\n");

            if (tessellate) {
                output.append(prefix + "\t<tessellate>1</tessellate>\n");
            }

            super.write(prefix + "\t", output);
            output.append(prefix + "\t<coordinates>");

            for (String coordinate : coordinates) {
                output.append(lineEnding + coordinate);
                lineEnding = "\n" + prefix + "\t";
            }

            output.append("</coordinates>\n");
            output.append(prefix + "</LineString>\n");
        }

        /** Should we tessellate (or follow the contour of the terrain)?. */
        private boolean tessellate;
        /** List of coordinate strings in "long, lat, alt" format. */
        private java.util.List<String> coordinates;
    }

    /** A point or icon on the map. */
    public static class Point extends TerrainElement {

        /**
            @param latitudePoint the latitude of the point
            @param longitudePoint the longitude of the point
            @param altitudePoint the altitude of the point
        */
        public Point(double latitudePoint, double longitudePoint, double altitudePoint) {
            latitude = latitudePoint;
            longitude = longitudePoint;
            altitude = altitudePoint;
            extrude = false;
        }

        /** Specifies whether to connect the LinearRing to the ground.
            To extrude this geometry, the altitude mode must be either
                relativeToGround, relativeToSeaFloor, or absolute.
            Only the vertices of the LinearRing are extruded, not the center of the geometry.
            The vertices are extruded toward the center of the Earth's sphere.
            @return this for call chaining
        */
        public Point extrude() {
            extrude = true;
            return this;
        }

        /** Writes out the Point.
            Coordinates are written out in the order they were added, in long, lat, alt groups.
            @param prefix The indentation
            @param output The stream to write to
            @throws IOException on io error
        */
        @Override
        public void write(String prefix, FileWriter output) throws IOException {
            output.append(prefix + "<Point>\n");

            if (extrude) {
                output.append(prefix + "\t<extrude>1</extrude>\n");
            }

            super.write(prefix + "\t", output);

            output.append(prefix + "\t<coordinates>" + longitude
                        + "," + latitude
                        + "," + altitude
                        + "</coordinates>\n");
            output.append(prefix + "</Point>\n");
        }

        /** The latitude of the icon. */
        private double latitude;
        /** The longitude of the icon. */
        private double longitude;
        /** The altitude of the icon. */
        private double altitude;
        /** Connect the point to the ground. */
        private boolean extrude;
    }

    /** The area to look at. */
    public static class LookAt implements Item {

        /**
            Defaults to a range of 100. Heading defaults to 0.
            @param latitudeCenter center latitude to look at
            @param longitudeCenter center longitude to look at
        */
        public LookAt(double latitudeCenter, double longitudeCenter) {
            final int oneHundredMileRange = 100;
            latitude = latitudeCenter;
            longitude = longitudeCenter;
            heading = 0;
            tilt = 0;
            range = oneHundredMileRange;
            altitude = 0;
        }

        /**
            @param newHeading the heading to use.
            @return this for call chaining
        */
        public LookAt setHeading(double newHeading) {
            heading = newHeading;
            return this;
        }

        /**
            @param newRange the range to use.
            @return this for call chaining
        */
        public LookAt setRange(double newRange) {
            range = newRange;
            return this;
        }

        /**
            @param newTilt the tilt to use.
            @return this for call chaining
        */
        public LookAt setTilt(double newTilt) {
            tilt = newTilt;
            return this;
        }

        /**
            @param newAltitude the altitude to use.
            @return this for call chaining
        */
        public LookAt setAltitude(double newAltitude) {
            altitude = newAltitude;
            return this;
        }

        /** Writes out the LookAt.
            @param prefix The indentation
            @param output The stream to write to
            @throws IOException on io error
        */
        public void write(String prefix, FileWriter output) throws IOException {
            output.append(prefix + "<LookAt>\n");
            output.append(prefix + "\t<longitude>" + longitude + "</longitude>\n");
            output.append(prefix + "\t<latitude>" + latitude + "</latitude>\n");
            output.append(prefix + "\t<altitude>" + altitude + "</altitude>\n");
            output.append(prefix + "\t<heading>" + heading + "</heading>\n");
            output.append(prefix + "\t<tilt>" + tilt + "</tilt>\n");
            output.append(prefix + "\t<range>" + range + "</range>\n");
            output.append(prefix + "</LookAt>\n");
        }

        /** The center latitude. */
        private double latitude;
        /** The center longitude. */
        private double longitude;
        /** The center altitude. */
        private double altitude;
        /** The heading. */
        private double heading;
        /** The tilt. */
        private double tilt;
        /** The range. Defaults to 100. */
        private double range;
    }

    /** Parent for tags that contain other items. */
    public abstract static class List extends ArrayList<Item> implements Item {

        /**
            @param tagType The tag type
            @param tagAttributes The attributes to use,
                    or null if there are no attributes. Attributes are of the form x="y".
        */
        public List(String tagType, String tagAttributes) {
            tag = tagType;
            attributes = tagAttributes;
        }

        /** Writes out the tag and its contained items.
            @param prefix The indentation
            @param output The stream to write to
            @throws IOException on io error
        */
        public void write(String prefix, FileWriter output) throws IOException {
            output.append(prefix + "<" + tag + null == attributes
                                                    ? ""
                                                    : (" " + attributes)
                                + ">\n");

            for (Item item : this) {
                item.write(prefix + "\t", output);
            }

            output.append(prefix + "</" + tag + ">\n");
        }

        /** The tag type. */
        private String tag;
        /** The attributes for the tag of the form 'attribute="value"'. */
        private String attributes;
    }

    /** Folder tag. */
    public static class Folder extends List {

        /** Create a new folder tag. */
        public Folder() {
            super("Folder", null);
        }

        /**
            @param item The sub-item to add to the list.
            @return this for call chaining
        */
        public Folder append(Item item) {
            add(item);
            return this;
        }

    }

    /** Placemark tag (used for icons). */
    public static class Placemark extends List {

        /** Create Placemark tag. */
        public Placemark() {
            super("Placemark", null);
        }

        /**
            @param item The sub-item to add to the list.
            @return this for call chaining
        */
        public Placemark append(Item item) {
            add(item);
            return this;
        }

    }

    /** IconStyle tag. */
    public static class StyleIcon implements Item {

        /**
            @param iconUrl The url of the icon
        */
        public StyleIcon(String iconUrl) {
            url = iconUrl;
        }

        /** Writes out the IconStyle.
            @param prefix The indentation
            @param output The stream to write to
            @throws IOException on io error
        */
        public void write(String prefix, FileWriter output) throws IOException {
            output.append(prefix + "<IconStyle>\n");
            output.append(prefix + "\t<Icon>\n");
            output.append(prefix + "\t\t<href><![CDATA[" + url + "]]></href>\n");
            output.append(prefix + "\t</Icon>\n");
            output.append(prefix + "</IconStyle>\n");
        }

        /** The url of the icon. */
        private String url;

    }

    /** Width tag (for lines). */
    public static class StyleWidth extends Simple {

        /**
            @param width The width of the line for style.
        */
        public StyleWidth(double width) {
            super("width", "" + width);
        }

    }

    /** Color tag (for lines). */
    public static class StyleColor extends Simple {

        /**
            @param color The color of the line for style.
        */
        public StyleColor(String color) {
            super("color", color);
        }

    }

    /** LineStyle container. */
    public static class LineStyle extends List {

        /** New LineStyle tag. */
        public LineStyle() {
            super("LineStyle", null);
        }

        /**
            @param item The sub-item to add to the list.
            @return this for call chaining
        */
        public LineStyle append(Item item) {
            add(item);
            return this;
        }

    }

    /** Style for polygons. */
    public static class PolyStyle extends List {

        /** New PolyStyle tag. */
        public PolyStyle() {
            super("PolyStyle", null);
        }

        /**
            @param item The sub-item to add to the list.
            @return this for call chaining
        */
        public PolyStyle append(Item item) {
            add(item);
            return this;
        }

    }

    /** Style definition tag. */
    public static class Style extends List {

        /**
            @param name The id name for the style
        */
        public Style(String name) {
            super("Style", "id=\"" + name + "\"");
        }

        /**
            @param item The sub-item to add to the list.
            @return this for call chaining
        */
        public Style append(Item item) {
            add(item);
            return this;
        }

    }

    /** Open = true tag. */
    public static class Open extends Simple {

        /** Creates a &lt;open&gt;1&lt;/open&gt; tag. */
        public Open() {
            super("open", "1");
        }

    }

    /** Document definition. */
    public static class Document extends List {

        /** Create new document. */
        public Document() {
            super("Document", null);
        }

        /**
            @param item The sub-item to add to the list.
            @return this for call chaining
        */
        public Document append(Item item) {
            add(item);
            return this;
        }

    }

    /** Write the document to a .kml file.
        @param filename The path to the .kml file to create
        @param document The document structure to write out
        @throws IOException on io error
    */
    public static void write(String filename, Document document) throws IOException {
        FileWriter output = new FileWriter(filename);

        output.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        output.append("<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n");

        document.write("\t", output);
        output.append("</kml>\n");
        output.close();
    }

}
