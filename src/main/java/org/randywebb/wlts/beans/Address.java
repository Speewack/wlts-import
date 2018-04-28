/**
 *
 */
package org.randywebb.wlts.beans;

import java.util.List;
import java.util.ArrayList;

import org.randywebb.wlts.beans.AbstractBean;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author randyw
 *
 */
public class Address extends AbstractBean {
  private static Logger log = LoggerFactory.getLogger(Address.class);

	public static List<Address> fromArray(JSONArray array) {
		return fromArray(array, new ArrayList<Address>(), Address.class);
	}

	public Address() {
	}

	public Address(JSONObject definition) {
		update(definition, new String[] {"latitude", "longitude", "postalCode", "state", "desc1", "desc2", "desc3", "includeLatLong"});

		if (!containsKey("streetAddress") || (null != get("desc1")) && (null != get("desc2"))) {
			setStreetAddress(get("desc1") + ", " + get("desc2"));
		}
	}

  /**
   * @return the streetAddress
   */
  public String getStreetAddress() {
    return get("streetAddress");
  }

  /**
   * @param streetAddress
   *          the streetAddress to set
   */
  public void setStreetAddress(String streetAddress) {
    put("streetAddress", null == streetAddress ? null : streetAddress.trim());
  }

  /**
   * @return the city
   */
  public String getCity() {
    return get("city");
  }

  /**
   * @param city
   *          the city to set
   */
  public void setCity(String city) {
    put("city", null == city ? null : city.trim());
  }

  /**
   * @return the state
   */
  public String getState() {
    return get("state");
  }

  /**
   * @param state
   *          the state to set
   */
  public void setState(String state) {
    put("state", null == state ? null : state.trim());
  }

  /**
   * @return the postalCode
   */
  public String getPostalCode() {
    return get("postalCode");
  }

  /**
   * @param postalCode
   *          the postalCode to set
   */
  public void setPostalCode(String postalCode) {
    put("postalCode", null == postalCode ? null : postalCode.trim());
  }

  /**
   * @return the longitude
   */
  public double getLongitudeValue() {
    return null == getLongitude() ? null : Double.parseDouble(getLongitude());
  }

  /**
   * @return the longitude
   */
  public String getLongitude() {
    return get("longitude");
  }

  /**
   * @param longitude
   *          the longitude to set
   */
  public void setLongitude(String longitude) {
    put("longitude", null == longitude ? null : longitude.trim());
  }

  /**
   * @return the latitude
   */
  public Double getLatitudeValue() {
    return null == getLatitude() ? null : Double.parseDouble(getLatitude());
  }

  /**
   * @return the latitude
   */
  public String getLattitude() {
    return getLatitude();
  }

  /**
   * @return the latitude
   */
  public String getLatitude() {
    return get("latitude");
  }

  /**
   * @param latitude
   *          the latitude to set
   */
  public void setLattitude(String latitude) {
    setLatitude(latitude);
  }

  /**
   * @param latitude
   *          the latitude to set
   */
  public void setLatitude(String latitude) {
    put("latitude", null == latitude ? null : latitude.trim());
  }

  /**
   * @return the includeLatLong
   */
  public boolean isIncludeLatLong() {
    return get("includeLatLong").substring(0,1).equalsIgnoreCase("t");
  }

  /**
   * @param includeLatLong
   *          the includeLatLong to set
   */
  public void setIncludeLatLong(boolean includeLatLong) {
    put("includeLatLong", includeLatLong ? "true" : "false");
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "Address [" + super.toString() + "]";
  }

  /**
   * Parse an Address String from MLS into separate Address Fields
   * @param addressString MLS Address String
   * @return The address parsed
   */
  public static Address toAddress(String addressString) {
    Address a = new Address();

    try {
      StringBuilder sb = new StringBuilder(addressString);

      // Delimit address components with comma
      sb.replace(sb.lastIndexOf(" "), sb.lastIndexOf(" "), ", "); // Fix zipcode
      sb.replace(sb.lastIndexOf("<"), sb.lastIndexOf(">") + 1, ", "); // Replace last <br /> with
                                                                      // comma

      while (sb.indexOf("<") > 0 && sb.indexOf(">") > 0) {
        sb.replace(sb.indexOf("<"), sb.indexOf(">") + 1, " "); // Replace remaining <br /> with
                                                               // space
      }

      // System.out.println(sb.toString());

      String[] addrComponent = sb.toString().split(",");

      // TODO: I may need to make this more robust at some point if the data structure changes or if
      // we don't always have 4 components
      a.setStreetAddress(addrComponent[0]);
      a.setCity(addrComponent[1]);
      a.setState(addrComponent[2]);
      a.setPostalCode(addrComponent[3]);
    } catch (NullPointerException npe) {
      if (log.isInfoEnabled()) {
        log.info("Unable to parse address from \"" + addressString + "\"", npe);
      }
    } catch (ArrayIndexOutOfBoundsException e) {
      if (log.isInfoEnabled()) {
        log.info("Unable to parse address from \"" + addressString + "\"", e);
      }
    }
    return a;
  }

  public static void main(String[] args) {
    System.out.println(Address.toAddress("17413 Toyahville Trl<br />Round Rock, Texas 78664"));
    System.out.println(Address.toAddress("16101 White River Blvd<br />Apt 21101<br />Pflugerville, Texas 78660-0006"));
  }

}
