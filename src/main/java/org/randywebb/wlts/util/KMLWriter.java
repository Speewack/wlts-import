package org.randywebb.wlts.util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
	@todo BalloonStyle
*/
public class KMLWriter {

  public interface Item {
  	public void write(String prefix, FileWriter output) throws IOException;
  };

  public static class Simple implements Item {

  	public Simple(String tag, String value) {
  		this.tag = tag;
  		this.value = value;
  	}

  	public void write(String prefix, FileWriter output) throws IOException {
		output.append(prefix + "<"+tag+"><![CDATA["+value+"]]></"+tag+">\n");
  	}

  	private String tag;
  	private String value;

  }

  public static class Name extends Simple {
  	public Name(String name) {
  		super("name", name);
  	}
  }

  public static class Description extends Simple {
  	public Description(String description) {
  		super("description", description);
  	}
  }

  public static class Invisible extends Simple {
  	public Invisible() {
  		super("visibility", "0");
  	}
  }

  public static class UseStyle extends Simple {
  	public UseStyle(String name) {
  		super("styleUrl", "#"+name);
  	}
  }

  public static class Point implements Item {

  	public Point(double latitude, double longitude, double altitude) {
  		this.latitude = latitude;
  		this.longitude = longitude;
  		this.altitude = altitude;
  		extrude = false;
  		altitude_mode = null;
  	}

	public Point extrude() {
		extrude = true;
		return this;
	}

	public Point altitudeMode(String mode) {
		altitude_mode = mode;
		return this;
	}

	public Point altitudeRelativeToGround() {
		return altitudeMode("relativeToGround");
	}

	public Point altitudeAbsolute() {
		return altitudeMode("absolute");
	}

  	public void write(String prefix, FileWriter output) throws IOException {
		output.append(prefix + "<Point>\n");

		if (extrude) {
			output.append(prefix + "\t<extrude>1</extrude>\n");
		}

		if (null != altitude_mode) {
			output.append(prefix + "\t<altitudeMode>"+altitude_mode+"</altitudeMode>\n");
		}

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
