package org.randywebb.wlts.util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Makes generating .kml files a bit easier.
	Note: You still have to know how to format a .kml file, this class just mirrors the XML.
*/
public class KMLWriter {

	/** Any element that needs to be written to the file. */
	public interface Item {
		/** Writes the element to the output.
			@param prefix The indentation level
			@param output The stream to write the element to
			@throws IOException on io errors
		*/
		public void write(String prefix, FileWriter output) throws IOException;
	};

	/** A simple tag. It just has &lt;tag&gt;value&lt;tag&gt; */
	public static class Simple implements Item {

		/**
			@param tag The tag type
			@param value the value for the tag
		*/
		public Simple(String tag, String value) {
			this.tag = tag;
			this.value = value;
		}

		/** Writes the tag to the output.
			@param prefix The indentation level
			@param output The stream to write the element to
			@throws IOException on io errors
		*/
		public void write(String prefix, FileWriter output) throws IOException {
			output.append(prefix + "<"+tag+"><![CDATA["+value+"]]></"+tag+">\n");
		}

		/** The type of tag */
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

	/** Description element */
	public static class Description extends Simple {
		/** @param description The description to add */
		public Description(String description) {
			super("description", description);
		}
	}

	/** Mark the current object invisible */
	public static class Invisible extends Simple {
		/** visibility defaults to 1, so to make invisible, add a visibility=0 tag */
		public Invisible() {
			super("visibility", "0");
		}
	}

	/** Use a style URL */
	public static class UseStyle extends Simple {
		/** @param name the name of the style (will use anchor url, ie #name) */
		public UseStyle(String name) {
			super("styleUrl", "#"+name);
		}
	}

	/** General category for items that have altitude mode */
	public static abstract class TerrainElement implements Item {

		/** A line that does not tessellate and has no altitude mode and no coordinates */
		public TerrainElement() {
			altitude_mode = null;
		}

		/** Sets the altitude mode.
			Preferred to use altitudeRelativeToGround() or altitudeAbsolute().
			@param mode The mode name to set
			@return this for call chaining.
		*/
		public TerrainElement altitudeMode(String mode) {
			altitude_mode = mode;
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
			if (null != altitude_mode) {
				output.append(prefix + "\t<altitudeMode>"+altitude_mode+"</altitudeMode>\n");
			}
		}

		/** What altitude mode should we use (or null for do not specify) */
		protected String altitude_mode;
	}

	/** Add a LineString to the map. */
	public static class Line extends TerrainElement {

		/** A line that does not tessellate and has no altitude mode and no coordinates */
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
			coordinates.add(""+longitude+","+latitude+","+altitude);
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

		/** Should we tessellate (or follow the contour of the terrain)? */
		private boolean tessellate;
		/** What altitude mode should we use (or null for do not specify) */
		private String altitude_mode;
		/** List of coordinate strings in "long, lat, alt" format */
		private ArrayList<String> coordinates;
	}

	/** A point or icon on the map. */
	public static class Point extends TerrainElement {

		/**
			@param latitude the latitude of the point
			@param longitude the longitude of the point
			@param altitude the altitude of the point
		*/
		public Point(double latitude, double longitude, double altitude) {
			this.latitude = latitude;
			this.longitude = longitude;
			this.altitude = altitude;
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

			output.append(prefix + "\t<coordinates>"+longitude+","+latitude+","+altitude+"</coordinates>\n");
			output.append(prefix + "</Point>\n");
		}

		private double latitude;
		private double longitude;
		private double altitude;
		private boolean extrude;
		private String altitude_mode;
	}

	public static class LookAt implements Item {

	public LookAt(double latitude, double longitude) {
	this.latitude = latitude;
	this.longitude = longitude;
	this.heading = 0;
	this.tilt = 0;
	this.range = 100;
	this.altitude = 0;
	}

	public LookAt setHeading(double heading) {
	this.heading = heading;
	return this;
	}

	public LookAt setRange(double range) {
	this.range = range;
	return this;
	}

	public LookAt setTilt(double tilt) {
	this.tilt = tilt;
	return this;
	}

	public LookAt setAltitude(double altitude) {
	this.altitude = altitude;
	return this;
	}

	public void write(String prefix, FileWriter output) throws IOException {
	output.append(prefix + "<LookAt>\n");
	output.append(prefix + "\t<longitude>"+longitude+"</longitude>\n");
	output.append(prefix + "\t<latitude>"+latitude+"</latitude>\n");
	output.append(prefix + "\t<altitude>"+altitude+"</altitude>\n");
	output.append(prefix + "\t<heading>"+heading+"</heading>\n");
	output.append(prefix + "\t<tilt>"+tilt+"</tilt>\n");
	output.append(prefix + "\t<range>"+range+"</range>\n");
	output.append(prefix + "</LookAt>\n");
	}

	private double latitude;
	private double longitude;
	private double heading;
	private double tilt;
	private double range;
	private double altitude;
	}

	public static class List extends ArrayList<Item> implements Item {

	public List(String tag, String attributes) {
	this.tag = tag;
	this.attributes = attributes;
	}

	public void write(String prefix, FileWriter output) throws IOException {
	output.append(prefix + "<"+tag+(null == attributes ? "" : (" " + attributes))+">\n");

	for (Item item : this) {
	item.write(prefix + "\t", output);
	}

	output.append(prefix + "</"+tag+">\n");
	}

	private String tag;
	private String attributes;
	}

	public static class Folder extends List {

	public Folder() {
	super("Folder", null);
	}

	public Folder append(Item item) {
	add(item);
	return this;
	}

	}

	public static class Placemark extends List {

	public Placemark() {
	super("Placemark", null);
	}

	public Placemark append(Item item) {
	add(item);
	return this;
	}

	}

	public static class StyleIcon implements Item {

	public StyleIcon(String url) {
	this.url = url;
	}

	public void write(String prefix, FileWriter output) throws IOException {
	output.append(prefix + "<IconStyle>\n");
	output.append(prefix + "\t<Icon>\n");
	output.append(prefix + "\t\t<href><![CDATA["+url+"]]></href>\n");
	output.append(prefix + "\t</Icon>\n");
	output.append(prefix + "</IconStyle>\n");
	}

	private String url;

	}

	public static class StyleWidth extends Simple {
	public StyleWidth(double width) {
	super("width", ""+width);
	}
	}

	public static class StyleColor extends Simple {
	public StyleColor(String color) {
	super("color", color);
	}
	}

	public static class LineStyle extends List {

	public LineStyle() {
	super("LineStyle", null);
	}

	public LineStyle append(Item item) {
	add(item);
	return this;
	}

	}

	public static class PolyStyle extends List {

	public PolyStyle() {
	super("PolyStyle", null);
	}

	public PolyStyle append(Item item) {
	add(item);
	return this;
	}

	}

	public static class Style extends List {

	public Style(String name) {
	super("Style", "id=\""+name+"\"");
	}

	public Style append(Item item) {
	add(item);
	return this;
	}

	}

	public static class Open extends Simple {
	public Open() {
	super("open", "1");
	}
	}

	public static class Document extends List {

	public Document() {
	super("Document", null);
	}

	public Document append(Item item) {
	add(item);
	return this;
	}

	}

	public static void write(String filename, Document document) throws IOException {
	FileWriter output = new FileWriter(filename);

	output.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
	output.append("<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n");

	document.write("\t", output);
	output.append("</kml>\n");
	output.close();
	}

	private static Logger log = LoggerFactory.getLogger(KMLWriter.class);
	private FileWriter output;
	private Folder currentFolder;
}
