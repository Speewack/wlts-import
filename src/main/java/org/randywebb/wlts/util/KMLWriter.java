package org.randywebb.wlts.util;

import java.io.FileWriter;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KMLWriter {

  public class Folder {

  	public Folder(String name, String description) throws IOException {
  		if (null != currentFolder) {
  			currentFolder.close();
  		}
	  	output.append("\t\t<Folder>\n");
	  	output.append("\t\t\t<name>"+name+"</name>\n");
	  	output.append("\t\t\t<description>"+description+"</description>\n");
  	}

  	public Folder lookAt(double latitude, double longitude, double heading, double tilt, double range, double altitude) throws IOException {
	  	output.append("\t\t\t<LookAt>\n");
	  	output.append("\t\t\t\t<longitude>"+longitude+"</longitude>\n");
	  	output.append("\t\t\t\t<latitude>"+latitude+"</latitude>\n");
	  	output.append("\t\t\t\t<altitude>"+altitude+"</altitude>\n");
	  	output.append("\t\t\t\t<heading>"+heading+"</heading>\n");
	  	output.append("\t\t\t\t<tilt>"+tilt+"</tilt>\n");
	  	output.append("\t\t\t\t<range>"+range+"</range>\n");
	  	output.append("\t\t\t</LookAt>\n");
	  	return this;
  	}

	public Folder placemark(String name, String description, double latitude, double longitude, String icon) throws IOException {
	  	output.append("\t\t\t<Placemark>\n");
	  	output.append("\t\t\t\t<name>"+name+"</name>\n");
	  	output.append("\t\t\t\t<description>"+description+"</description>\n");
	  	output.append("\t\t\t\t<Point>\n");
	  	output.append("\t\t\t\t\t<coordinates>"+latitude+","+longitude+"</coordinates>\n");
	  	output.append("\t\t\t\t</Point>\n");
	  	if (null != icon) {
			output.append("\t\t\t\t<styleUrl>#"+icon+"</styleUrl>\n");
		}
	  	output.append("\t\t\t</Placemark>\n");
	  	return this;
	}

	public Folder placemark(String name, String description, double latitude, double longitude) throws IOException {
		return placemark(name, description, latitude, longitude, null);
	}

  	public void close() throws IOException {
	  	output.append("\t\t</Folder>\n");
	  	currentFolder = null;
  	}

  	private void check() throws IOException {
  		if (currentFolder != this) {
  			throw new IOException("Folder was closed or new folder started");
  		}
  	}

  };

  public KMLWriter(String filename, String documentName, String description) throws IOException {
  	output = new FileWriter(filename);
  	output.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
  	output.append("<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n");
  	output.append("\t<Document>\n");
  	output.append("\t\t<name>"+documentName+"</name>\n");
  	output.append("\t\t<open>1</open>\n");
  	output.append("\t\t<description>"+description+"</description>\n");
  	currentFolder = null;
  }

  public KMLWriter addIcon(String name, String imageUrl) throws IOException {
  	output.append("\t\t<Style id=\""+name+"\">\n");
  	output.append("\t\t\t<IconStyle>\n");
  	output.append("\t\t\t\t<Icon>\n");
  	output.append("\t\t\t\t\t<href>"+imageUrl+"</href>\n");
  	output.append("\t\t\t\t</Icon>\n");
  	output.append("\t\t\t</IconStyle>\n");
  	output.append("\t\t</Style>\n");
  	return this;
  }

  public KMLWriter addLineStyle(String name, String lineColor, int lineWidth, String polyColor) throws IOException {
  	output.append("\t\t<Style id=\""+name+"\">\n");
  	if ( (lineWidth >= 0) || (null != lineColor) ) {
		output.append("\t\t\t<LineStyle>\n");
		if (null != lineColor) {
			output.append("\t\t\t\t<color>"+lineColor+"</color>\n");
		}
		if (lineWidth >= 0) {
			output.append("\t\t\t\t<width>"+lineWidth+"</width>\n");
		}
		output.append("\t\t\t</LineStyle>\n");
	}
  	if (null != polyColor) {
		output.append("\t\t\t<PolyStyle>\n");
		output.append("\t\t\t\t<color>"+polyColor+"</color>\n");
		output.append("\t\t\t</PolyStyle>\n");
	}
  	output.append("\t\t</Style>\n");
  	return this;
  }

  public KMLWriter addLineStyle(String name, String lineColor, int lineWidth) throws IOException {
  	return addLineStyle(name, lineColor, lineWidth, null);
  }

  public KMLWriter addLineStyle(String name, String lineColor) throws IOException {
  	return addLineStyle(name, lineColor, -1, null);
  }

  public KMLWriter addLineStyle(String name, String lineColor, String polyColor) throws IOException {
  	return addLineStyle(name, lineColor, -1, polyColor);
  }

  public void close() throws IOException {
    if (null != currentFolder) {
    	currentFolder.close();
    }
  	output.append("\t</Document>\n");
  	output.append("</kml>\n");
  	output.close();
  }

  private static Logger log = LoggerFactory.getLogger(KMLWriter.class);
  private FileWriter output;
  private Folder currentFolder;
}
