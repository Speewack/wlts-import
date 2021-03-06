package org.randywebb.wlts.beans;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TestAbstractBean {

    private static class TestCase extends AbstractBean {
        public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
        public TestCase() {
            super(DATE_FORMAT, null, null, null);
        }
    }

    @Test
    @DisplayName("Test setters and getters")
    void testJSON() {
        try {
            TestCase    test = new TestCase();
            Date        now = TestCase.DATE_FORMAT.parse(TestCase.DATE_FORMAT.format(new Date()));

            test.put("int", 5);
            assertEquals(test.getInteger("int").intValue(), 5);

            test.put("long", 6L);
            assertEquals(test.getLong("long").longValue(), 6);

            test.put("double", 3.14);
            assertEquals(test.getDouble("double").doubleValue(), 3.14);

            test.put("boolean", true);
            assertTrue(test.getBoolean("boolean").booleanValue());

            test.put("date", now);
            assertTrue(test.getDate("date").equals(now));
        } catch(ParseException e) {
            e.printStackTrace();
            fail("Exception parsing date: " + e.getMessage());
        }
    }

}
