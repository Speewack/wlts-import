package org.randywebb.wlts.beans;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Represents an Address.
* @author randyw
*
*/
public class Address extends AbstractBean {

    /** Log errors. */
    private static Logger log = LoggerFactory.getLogger(Address.class);

    /** Converts a JSON Array of addresses to a List of Address.
        @param array JSON Array of JSON address objects
        @return The Addresses from the JSON Array
    */
    public static List<Address> fromArray(JSONArray array) {
        return fromArray(array, new ArrayList<Address>(), Address.class);
    }

    /** Default constructor. */
    public Address() {
    }

    /** Convert an address JSON Object to an Address.
        unit members and callings v2
            desc1 is the street address
            desc2 is the unit number if there is a desc3, otherwise it is the city, state, and zip
            desc3, if present, is the city, state, and zip
        ministering members
            streetAddress is the street address
            streetAddress2 is the unit number
        @param definition A JSON address Object
    */
    public Address(JSONObject definition) {
        update(definition, "streetAddress", "streetAddress2", "latitude", "longitude", "postalCode", "postal", "city", "state", "desc1", "desc2", "desc3", "includeLatLong");

        if ((null == get("streetAddress")) && (null != get("desc1")) && (null != get("desc2"))) {
            setStreetAddress(get("desc1"));
        }

        if ((null == get("streetAddress2")) && (null != get("desc2")) && (null != get("desc3"))) {
            setUnitNumber(get("desc2"));
        }

        if (containsKey("postal") && (null == get("postalCode"))) {
            setPostalCode(get("postal"));
        }

        if ((null == get("city")) && (null != get("desc2"))) {
            setCity((null == get("desc3") ? get("desc2") : get("desc3")).split(",")[0]);
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
    * @return the unit number
    */
    public String getUnitNumber() {
        return get("streetAddress2");
    }

    /**
    * @param unitNumber
    *          the unitNumber to set
    */
    public void setUnitNumber(String unitNumber) {
        put("streetAddress2", null == unitNumber ? null : unitNumber.trim());
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
    public Double getLongitudeValue() {
        return getDouble("longitude");
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
        setIncludeLatLong((null != getLatitude()) && (null != getLongitude()));
    }

    /**
    * @return the latitude
    */
    public Double getLatitudeValue() {
        return getDouble("latitude");
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
        setIncludeLatLong((null != getLatitude()) && (null != getLongitude()));
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
        return get("includeLatLong").substring(0, 1).equalsIgnoreCase("t");
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
    * Parse an Address String from MLS into separate Address Fields.
    * @todo I may need to make this more robust at some point if the data structure changes or if
    *            we don't always have 4 components
    * @param addressString MLS Address String
    * @return The address parsed
    */
    public static Address toAddress(String addressString) {
        Address a = new Address();

        if (log.isTraceEnabled()) {
            log.trace("Address: " + addressString);
        }
        try {
            StringBuilder sb = new StringBuilder(addressString);
            final int street = 0;
            final int city = 1;
            final int state = 2;
            final int postalCode = 3;

            // Delimit address components with comma
            sb.replace(sb.lastIndexOf(" "), sb.lastIndexOf(" "), ", "); // Fix zipcode

            // Replace last <br /> with comma
            sb.replace(sb.lastIndexOf("<"), sb.lastIndexOf(">") + 1, ", ");

            while (sb.indexOf("<") > 0 && sb.indexOf(">") > 0) {
                // Replace remaining <br /> with space
                sb.replace(sb.indexOf("<"), sb.indexOf(">") + 1, " ");
            }

            // System.out.println(sb.toString());

            String[] addrComponent = sb.toString().split(",");

            // TODO: I may need to make this more robust at some point if the
            // data structure changes or if we don't always have 4 components
            a.setStreetAddress(addrComponent[street]);
            a.setCity(addrComponent[city]);
            a.setState(addrComponent[state]);
            a.setPostalCode(addrComponent[postalCode]);
        } catch (NullPointerException
                    | ArrayIndexOutOfBoundsException
                    | StringIndexOutOfBoundsException e) {
            if (log.isInfoEnabled()) {
                log.info("Unable to parse address from \"" + addressString + "\"", e);
            }
        }
        return a;
    }
}
