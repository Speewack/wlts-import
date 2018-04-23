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

  public static class Name implements Item {

  	public Name(String name) {
  		this.name = name;
  	}

  	public void write(String prefix, FileWriter output) throws IOException {
		output.append(prefix + "<name>"+name+"</name>\n");
  	}

  	private String name;

  }

  public static class Description implements Item {

  	public Description(String description) {
  		this.description = description;
  	}

  	public void write(String prefix, FileWriter output) throws IOException {
		output.append(prefix + "<description>"+description+"</description>\n");
  	}

  	private String description;

  }

  public static class Invisible implements Item {

  	public Invisible() {
  	}

  	public void write(String prefix, FileWriter output) throws IOException {
		output.append(prefix + "<visibility>0</visibility>\n");
	}

  }

  public static class UseStyle implements Item {

  	public UseStyle(String name) {
  		this.name = name;
  	}

  	public void write(String prefix, FileWriter output) throws IOException {
		output.append(prefix + "<styleUrl>#"+name+"</styleUrl>\n");
	}

	private String name;

  }

  public static class Point {

  	public Point(double latitude, double longitude) {
  		this.latitude = latitude;
  		this.longitude = longitude;
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

		output.append(prefix + "\t<coordinates>"+latitude+","+longitude+"</coordinates>\n");
		output.append(prefix + "</Point>\n");
	}

  	private double latitude;
  	private double longitude;
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

  public static class Folder extends ArrayList<Item> implements Item {

  	public Folder() {
  	}

	public Folder append(Item item) {
		add(item);
		return this;
	}

  	public void write(String prefix, FileWriter output) throws IOException {
  		output.append(prefix + "<Folder>\n");

		for (Item item : this) {
			item.write(prefix + "\t", output);
		}

  		output.append(prefix + "</Folder>\n");
  	}

  }

  public static class Placemark extends ArrayList<Item> implements Item {

  	public Placemark() {
  	}

	public Placemark append(Item item) {
		add(item);
		return this;
	}

  	public void write(String prefix, FileWriter output) throws IOException {
  		output.append(prefix + "<Placemark>\n");

		for (Item item : this) {
			item.write(prefix + "\t", output);
		}

  		output.append(prefix + "</Placemark>\n");
  	}

  }

  public static class IconStyle implements Item {

  	public IconStyle(String url) {
  		this.url = url;
  	}

  	public void write(String prefix, FileWriter output) throws IOException {
		output.append(prefix + "<IconStyle>\n");
		output.append(prefix + "\t<Icon>\n");
		output.append(prefix + "\t\t<href>"+url+"</href>\n");
		output.append(prefix + "\t</Icon>\n");
		output.append(prefix + "</IconStyle>\n");
	}

	private String url;

  }

  public static class StyleWidth implements Item {

  	public StyleWidth(double width) {
  		this.width = width;
  	}

  	public void write(String prefix, FileWriter output) throws IOException {
		output.append(prefix + "<width>"+width+"</width>\n");
	}

	private double width;

  }

  public static class StyleColor implements Item {

  	public StyleColor(String color) {
  		this.color = color;
  	}

  	public void write(String prefix, FileWriter output) throws IOException {
		output.append(prefix + "<color>"+color+"</color>\n");
	}

	private String color;

  }

  public static class LineStyle extends ArrayList<Item> implements Item {

  	public LineStyle() {
  	}

	public LineStyle append(Item item) {
		add(item);
		return this;
	}

  	public void write(String prefix, FileWriter output) throws IOException {
  		output.append(prefix + "<LineStyle>\n");

		for (Item item : this) {
			item.write(prefix + "\t", output);
		}

  		output.append(prefix + "</LineStyle>\n");
  	}

  }

  public static class PolyStyle extends ArrayList<Item> implements Item {

  	public PolyStyle() {
  	}

	public PolyStyle append(Item item) {
		add(item);
		return this;
	}

  	public void write(String prefix, FileWriter output) throws IOException {
  		output.append(prefix + "<PolyStyle>\n");

		for (Item item : this) {
			item.write(prefix + "\t", output);
		}

  		output.append(prefix + "</PolyStyle>\n");
  	}

  }

  public static class Style extends ArrayList<Item> implements Item {

  	public Style(String name) {
  		this.name = name;
  	}

	public Style append(Item item) {
		add(item);
		return this;
	}

  	public void write(String prefix, FileWriter output) throws IOException {
  		output.append(prefix + "<Style id=\""+name+"\">\n");

		for (Item item : this) {
			item.write(prefix + "\t", output);
		}

  		output.append(prefix + "</Style>\n");
  	}

  	private String name;

  }

  public static void write(String filename, String name, String description, List<Style> styles, List<Folder> folders) throws IOException {
  	String prefix = "";
  	FileWriter output = new FileWriter(filename);

  	output.append(prefix + "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
  	output.append(prefix + "<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n");

  	prefix += "\t";
  	output.append(prefix + "<Document>\n");

  	prefix += "\t";
  	output.append(prefix + "<name>"+name+"</name>\n");
  	output.append(prefix + "<open>1</open>\n");
  	output.append(prefix + "<description>"+description+"</description>\n");

  	for (Style style : styles) {
  		style.write(prefix, output);
  	}

  	for (Folder folder : folders) {
  		folder.write(prefix, output);
  	}

  	prefix = prefix.substring(0, prefix.length() - 1);
  	output.append(prefix+"</Document>\n");
  	prefix = prefix.substring(0, prefix.length() - 1);
  	output.append(prefix+"</kml>\n");
  	output.close();
  }

  private static Logger log = LoggerFactory.getLogger(KMLWriter.class);
  private FileWriter output;
  private Folder currentFolder;
}
