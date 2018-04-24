/**
 *
 */
package org.randywebb.wlts.beans;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author randyw
 *
 */
public class Address {
  private static Logger log = LoggerFactory.getLogger(Address.class);

  private String streetAddress;
  private String city;
  private String State;
  private String postalCode;
  private String longitude;
  private String lattitude;
  private boolean includeLatLong;

  /**
   * @return the streetAddress
   */
  public String getStreetAddress() {
    return streetAddress;
  }

  /**
   * @param streetAddress
   *          the streetAddress to set
   */
  public void setStreetAddress(String streetAddress) {
    this.streetAddress = null == streetAddress ? null : streetAddress.trim();
  }

  /**
   * @return the city
   */
  public String getCity() {
    return city;
  }

  /**
   * @param city
   *          the city to set
   */
  public void setCity(String city) {
    this.city = null == city ? null : city.trim();
  }

  /**
   * @return the state
   */
  public String getState() {
    return State;
  }

  /**
   * @param state
   *          the state to set
   */
  public void setState(String state) {
    State = null == state ? null : state.trim();
  }

  /**
   * @return the postalCode
   */
  public String getPostalCode() {
    return postalCode;
  }

  /**
   * @param postalCode
   *          the postalCode to set
   */
  public void setPostalCode(String postalCode) {
    this.postalCode = null == postalCode ? null : postalCode.trim();
  }

  /**
   * @return the longitude
   */
  public String getLongitude() {
    return longitude;
  }

  /**
   * @param longitude
   *          the longitude to set
   */
  public void setLongitude(String longitude) {
    this.longitude = null == longitude ? null : longitude.trim();
  }

  /**
   * @return the lattitude
   */
  public String getLattitude() {
    return lattitude;
  }

  /**
   * @param lattitude
   *          the lattitude to set
   */
  public void setLattitude(String lattitude) {
    this.lattitude = null == lattitude ? null : lattitude.trim();
  }

  /**
   * @return the includeLatLong
   */
  public boolean isIncludeLatLong() {
    return includeLatLong;
  }

  /**
   * @param includeLatLong
   *          the includeLatLong to set
   */
  public void setIncludeLatLong(boolean includeLatLong) {
    this.includeLatLong = includeLatLong;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "Address [streetAddress=" + streetAddress + ", city=" + city + ", State=" + State + ", postalCode=" + postalCode + ", longitude=" + longitude + ", lattitude=" + lattitude
        + ", includeLatLong=" + includeLatLong + "]";
  }

  /**
   * Parse an Address String from MLS into separate Address Fields
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
