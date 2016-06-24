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
	 * @param streetAddress the streetAddress to set
	 */
	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}
	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}
	/**
	 * @param city the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}
	/**
	 * @return the state
	 */
	public String getState() {
		return State;
	}
	/**
	 * @param state the state to set
	 */
	public void setState(String state) {
		State = state;
	}
	/**
	 * @return the postalCode
	 */
	public String getPostalCode() {
		return postalCode;
	}
	/**
	 * @param postalCode the postalCode to set
	 */
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	/**
	 * @return the longitude
	 */
	public String getLongitude() {
		return longitude;
	}
	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	/**
	 * @return the lattitude
	 */
	public String getLattitude() {
		return lattitude;
	}
	/**
	 * @param lattitude the lattitude to set
	 */
	public void setLattitude(String lattitude) {
		this.lattitude = lattitude;
	}
	/**
	 * @return the includeLatLong
	 */
	public boolean isIncludeLatLong() {
		return includeLatLong;
	}
	/**
	 * @param includeLatLong the includeLatLong to set
	 */
	public void setIncludeLatLong(boolean includeLatLong) {
		this.includeLatLong = includeLatLong;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Address [streetAddress=" + streetAddress + ", city=" + city + ", State=" + State + ", postalCode="
				+ postalCode + ", longitude=" + longitude + ", lattitude=" + lattitude + ", includeLatLong="
				+ includeLatLong + "]";
	}
	
	
	
}
