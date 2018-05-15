package org.randywebb.wlts.beans;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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
