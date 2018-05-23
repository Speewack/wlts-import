package org.randywebb.wlts.beans;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

class TestAddress {

    @ParameterizedTest
    @CsvFileSource(resources = "/addresses.csv", numLinesToSkip = 1)
    @DisplayName("Validate various formats of address")
    void testToAddress(String description, String address, String expected) {
        assertEquals(expected, Address.toAddress(address).toString(), description);
    }

    @Test
    @DisplayName("Test JSON Parsing")
    void testAddressJSON() {
        try {
            Object obj = new JSONParser().parse(new InputStreamReader(ClassLoader.getSystemResourceAsStream("addresses.json")));
            List<Address> addresses = Address.fromArray( (JSONArray) obj );
            assertEquals(addresses.size(),8);
            assertEquals(addresses.get(0).getDouble("latitude").doubleValue(), 30.446545);
            assertEquals(addresses.get(0).getDouble("longitude").doubleValue(), -97.622255);
            assertEquals(addresses.get(0).getInteger("postalCode").intValue(), 78660);
            assertEquals(addresses.get(0).get("state"), "TX");

            // ministering members parsing

            assertEquals(addresses.get(1).getStreetAddress(), "1234 Soney cir");
            //assertEquals(addresses.get(1).get("streetAddress2"), "Apt 110"); this is in ministering members
            assertEquals(addresses.get(1).getPostalCode(), "93555");
            assertEquals(addresses.get(1).getState(), "CA");
            assertEquals(addresses.get(1).getCity(), "Ridgecrest");

            assertEquals(addresses.get(2).getStreetAddress(), "4321 W Charney Pkwy");
            //assertEquals(addresses.get(2).get("streetAddress2"), "Apt 110"); this is in ministering members
            assertEquals(addresses.get(2).getPostalCode(), "78754");
            assertEquals(addresses.get(2).getState(), "Texas");
            assertEquals(addresses.get(2).getCity(), "AUSTIN");

            assertEquals(addresses.get(3).getStreetAddress(), "9876 American Robin Path");
            //assertNull(addresses.get(3).get("streetAddress2")); this is in ministering members
            assertEquals(addresses.get(3).getPostalCode(), "93555-1436");
            assertEquals(addresses.get(3).getState(), "California");
            assertEquals(addresses.get(3).getCity(), "Ridgecrest");

            assertNull(addresses.get(4).getStreetAddress());
            //assertNull(addresses.get(4).get("streetAddress2")); this is in ministering members
            assertNull(addresses.get(4).getPostalCode());
            assertNull(addresses.get(4).getState());
            assertNull(addresses.get(4).getCity());

            // unit members and callings v2

            assertFalse(addresses.get(5).isIncludeLatLong());
            assertEquals(addresses.get(5).get("desc1"), "1234 Stoney Cir");
            assertEquals(addresses.get(5).get("desc2"), "Pflugerville, Texas 78660-4432");
            assertEquals(addresses.get(5).getStreetAddress(), "1234 Stoney Cir, Pflugerville, Texas 78660-4432");
            assertEquals(addresses.get(5).getState(), "Texas");
            assertEquals(addresses.get(5).getPostalCode(), "78660-4432");
            //assertEquals(addresses.get(5).getCity(), "Pflugerville"); we should be able to parse city out of desc2/3

            assertFalse(addresses.get(6).isIncludeLatLong());
            assertEquals(addresses.get(6).get("desc1"), "1234 Stoney Cir");
            assertEquals(addresses.get(6).get("desc2"), "Apt 456");
            assertEquals(addresses.get(6).get("desc3"), "Pflugerville, Texas 78660-4432");
            assertEquals(addresses.get(6).getStreetAddress(), "1234 Stoney Cir, Apt 456"); // Is this intentional behavior?
            assertEquals(addresses.get(6).getState(), "Texas");
            assertEquals(addresses.get(6).getPostalCode(), "78660-4432");
            //assertEquals(addresses.get(6).getCity(), "Pflugerville"); we should be able to parse city out of desc2/3

            assertTrue(addresses.get(7).isIncludeLatLong());
            assertEquals(addresses.get(7).get("desc1"), "1234 Stoney Cir");
            assertEquals(addresses.get(7).get("desc2"), "Apt 456");
            assertEquals(addresses.get(7).get("desc3"), "Pflugerville, Texas 78660-4432");
            assertEquals(addresses.get(7).getStreetAddress(), "1234 Stoney Cir, Apt 456"); // Is this intentional behavior?
            assertEquals(addresses.get(7).getState(), "Texas");
            assertEquals(addresses.get(7).getPostalCode(), "78660-4432");
            assertEquals(addresses.get(7).getLatitudeValue().doubleValue(), 30.446545);
            assertEquals(addresses.get(7).getLongitudeValue().doubleValue(), -97.622255);
            //assertEquals(addresses.get(7).getCity(), "Pflugerville"); we should be able to parse city out of desc2/3

        } catch(IOException | ParseException e) {
            e.printStackTrace();
            fail("Exception parsing Address JSON: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Validate Address Bean Properties")
    void testAddressFields() {
        Address a = Address.toAddress("1313 Mockingbird Ln<br />Dallas, Texas 90210");
        assertEquals("1313 Mockingbird Ln", a.getStreetAddress(), "Street Address Populated");
        assertEquals("Dallas", a.getCity(), "City Populated");
        assertEquals("Texas", a.getState(), "State Populated");
        assertNull(a.getLatitude());
        assertNull(a.getLatitudeValue());
        assertNull(a.getLongitude());
        assertNull(a.getLongitudeValue());
        assertEquals(a.getPostalCode(), "90210", "PostalCode Populated");

        String newCity="Marfa";
        String newLatitude="30.5086937";
        String newLattitude="30.4086937";
        String newLongitude="-97.7253762";
        String newPostalCode="12345";
        String newStreetAddress="4444 W. 15th Street";
        String newState="Colorado";

        a.setCity(newCity);
        assertEquals(newCity, a.getCity(), "City Setter");
        a.setLatitude(newLatitude);
        assertEquals(newLatitude, a.getLatitude(), "Latitude Setter");
        assertEquals(newLatitude, a.getLattitude(), "Latitude Setter");
        assertEquals(Double.parseDouble(newLatitude), a.getLatitudeValue().doubleValue(), "Latitude as Double");
        a.setLattitude(newLattitude);
        assertEquals(newLattitude, a.getLatitude(), "Latitude Setter");
        assertEquals(newLattitude, a.getLattitude(), "Latitude Setter");
        assertEquals(Double.parseDouble(newLattitude), a.getLatitudeValue().doubleValue(), "Latitude as Double");
        a.setLongitude(newLongitude);
        assertEquals(newLongitude, a.getLongitude(), "Longitude Setter");
        assertEquals(Double.parseDouble(newLongitude), a.getLongitudeValue().doubleValue(), "Longitude as Double");
        a.setPostalCode(newPostalCode);
        assertEquals(newPostalCode, a.getPostalCode(), "Postal Code Setter");
        a.setStreetAddress(newStreetAddress);
        assertEquals(newStreetAddress, a.getStreetAddress(), "Street Address Setter");
        a.setState(newState);
        assertEquals(newState, a.getState(), "State Setter");
    }

}
