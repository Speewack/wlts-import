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
	private String coupleName;
}
