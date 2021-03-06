package org.randywebb.wlts.beans;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.cellprocessor.ift.CellProcessor;

/** Abstract class for generic objects.
    Each object is actually a map of string to string.
    Getters and setters allow the strings to be interpreted in different formats.
    Getters and setters can also be used to allow types that do not easily convert to string.
*/
public abstract class AbstractBean extends HashMap<String, String> {

    /** log any errors or warnings. */
    private static Logger log = LoggerFactory.getLogger(AbstractBean.class);

    /** Field names which are Boolean. See forCSV() */
    private List<String> booleanFields;
    /** Field names which are Date. See forCSV() */
    private List<String> dateFields;
    /** Field names which are Integer. See forCSV() */
    private List<String> integerFields;
    /** Format for Date fields. See forCSV() */
    private SimpleDateFormat dateFormat;

    /** Initialize the default. */
    public AbstractBean() {
        this.booleanFields = new ArrayList<String>();
        this.dateFields = new ArrayList<String>();
        this.integerFields = new ArrayList<String>();
    }

    /** Initialize with fields that could be Date, Boolean, or Integer.
        @param dateFrmt Format to use to generate and parse Date strings
        @param booleanFieldList The names of the fields that are Booleans
        @param integerFieldList The names of the fields which are Integers
        @param dateFieldList The names of the fields which are Dates
    */
    public AbstractBean(SimpleDateFormat dateFrmt, List<String> booleanFieldList, List<String> integerFieldList, List<String> dateFieldList) {
        dateFormat = dateFrmt;
        booleanFields = null == booleanFieldList ? new ArrayList<String>() : booleanFieldList;
        dateFields = null == dateFieldList ? new ArrayList<String>() : dateFieldList;
        integerFields = null == integerFieldList ? new ArrayList<String>() : integerFieldList;
    }

    /** Helper for subclasses to convert JSON Array to List.
        See setFromJSON() for how subclasses can handle non-string values in the JSON
        @param <T> The type of the subclass
        @param array The JSON Array to parse
        @param toFill The List to fill with parsed objects
        @param clazz The Subclass.class. Would be T.class, if Java allowed that.
        @return List of subclass instances from the JSON array
    */
    protected static <T> List<T> fromArray(JSONArray array, List<T> toFill, Class<T> clazz) {

        if (null != array) {
            try {
                Constructor<T> constructor = clazz.getConstructor(JSONObject.class);

                for (Object o : array) {
                    try {
                        toFill.add(constructor.newInstance(new Object[] {(JSONObject) o}));
                    } catch (InvocationTargetException e) {
                        if (log.isErrorEnabled()) {
                            log.error("Cannot invoke " + clazz.getName() + "(JSONObject)", e);
                        }
                    } catch (IllegalAccessException e) {
                        if (log.isErrorEnabled()) {
                            log.error("No permissions to create " + clazz.getName() + "(JSONObject)", e);
                        }
                    } catch (InstantiationException e) {
                        if (log.isErrorEnabled()) {
                            log.error("Unable to create " + clazz.getName() + "(JSONObject)", e);
                        }
                    }
                }

            } catch (NoSuchMethodException e) {
                if (log.isErrorEnabled()) {
                    log.error("No constructor " + clazz.getName() + "(JSONObject)", e);
                }
            }
        }

        return toFill;
    }

    /** Updates the fields from a JSON Object.
        For use in subclass constructors.
        This is not a constructor because subclass version of setFromJSON would not be called.
        See setFromJSON.
        @param definition The JSON to pull values from
        @param expectedKeys The keys to pull from the JSON
        @return Returns this for call chaining
    */
    public AbstractBean update(JSONObject definition, String... expectedKeys) {

        for (String key : expectedKeys) {
            setFromJSON(definition, key);
        }

        return this;
    }

    /** Fill an array of default CSV CellProcessor for generation.
        For use by subclasses to have static method that does the same.
        @param headers The names of the headers for the CSV file
        @param booleanFields The names of the fields that are Booleans
        @param integerFields The names of the fields which are Integers
        @param dateFields The names of the fields which are Dates
        @param stringProcessor The processor for string fields
        @param booleanProcessor The processor for boolean fields
        @param integerProcessor The processor for integer fields
        @param dateProcessor The processor for date fields
        @return An array of CellProcessor to pass to CSV writer
    */
    protected static CellProcessor[] csvProcessors(String[] headers,
            List<String> booleanFields, List<String> dateFields, List<String> integerFields,
            CellProcessor stringProcessor, CellProcessor booleanProcessor,
            CellProcessor integerProcessor, CellProcessor dateProcessor) {

        ArrayList<CellProcessor> processors = new ArrayList<CellProcessor>();

        for (String header : headers) {
            if (booleanFields.contains(header)) {
                processors.add(booleanProcessor);
            } else if (integerFields.contains(header)) {
                processors.add(integerProcessor);
            } else if (dateFields.contains(header)) {
                processors.add(dateProcessor);
            } else {
                processors.add(stringProcessor);
            }
        }

        return processors.toArray(new CellProcessor[] {});
    }

    /** Get a type-correct map for CSV generation.
        All fields are String. The converts the Boolean, Date, and Integer fields to the
        appropriate type to be used for CSV generation.
        See booleanFields, dateFields, integerFields, and dateFormat.
        @return Type-correct map for CSV generation.
    */
    public Map<String, Object> forCSV() {
        HashMap<String, Object> results = new HashMap<String, Object>();

        for (Map.Entry<String, String> entry : entrySet()) {
            if (booleanFields.contains(entry.getKey())) {
                results.put(entry.getKey(), toBoolean(entry.getValue()));
            } else if (dateFields.contains(entry.getKey())) {
                results.put(entry.getKey(), toDate(entry.getValue()));
            } else if (integerFields.contains(entry.getKey())) {
                results.put(entry.getKey(), toInteger(entry.getValue()));
            } else {
                results.put(entry.getKey(), entry.getValue());
            }
        }

        return results;
    }

    /**
        @param name The name of the field to interpret as an integer
        @return The integer value of the field, or null if the field does not exist or is null
    */
    public Integer getInteger(String name) {
        return toInteger(get(name));
    }

    /**
        @param name The name of the field to set
        @param value The integer value for the field
    */
    public void put(String name, Integer value) {
        put(name, toString(value));
    }

    /**
        @param name The name of the field to interpret as an long integer
        @return The long integer value of the field, or null if the field does not exist or is null
    */
    public Long getLong(String name) {
        return toLong(get(name));
    }

    /**
        @param name The name of the field to set
        @param value The integer value for the field
    */
    public void put(String name, Long value) {
        put(name, toString(value));
    }

    /**
        @param name The name of the field to interpret as a double
        @return The integer value of the field, or null if the field does not exist or is null
    */
    public Double getDouble(String name) {
        return toDouble(get(name));
    }

    /**
        @param name The name of the field to set
        @param value The double value for the field
    */
    public void put(String name, Double value) {
        put(name, toString(value));
    }

    /**
        @param name The name of the field to interpret as a boolean
        @return The boolean value of the field, or null if the field does not exist or is null
    */
    public Boolean getBoolean(String name) {
        return toBoolean(get(name));
    }

    /**
        @param name The name of the field to set
        @param value The boolean value for the field
    */
    public void put(String name, Boolean value) {
        put(name, toString(value));
    }

    /**
        @param name The name of the field to interpret as a date
        @return The date value of the field, or null if the field does not exist or is null
    */
    public Date getDate(String name) {
        return toDate(get(name));
    }

    /**
        @param name The name of the field to set
        @param value The date value for the field
    */
    public void put(String name, Date value) {
        put(name, toString(value));
    }

    /** Helper method for subclasses to customize reading fields from JSON.
        Subclasses should check the key and handle special cases and in standard
        string cases call super.setFromJSON().
        @param definition The JSON object we're reading from
        @param key The key to create a value for.
    */
    protected void setFromJSON(JSONObject definition, String key) {
        Object value = definition.get(key);

        if ((null != value) || !containsKey(key)) {
            put(key, (null == value) ? null : value.toString());
        }

    }

    /**
        @param value String that contains a double value, or null
        @return The double value of the string
    */
    protected Double toDouble(String value) {
        return (null == value) ? null : Double.parseDouble(value);
    }

    /**
        @param value Double value or null
        @return The string representation of the double
    */
    protected String toString(Double value) {
        return (null == value) ? null : value.toString();
    }

    /** Helper method to convert String values to Boolean.
        @param value Boolean string
        @return Boolean value of string or null if string is null
    */
    protected Boolean toBoolean(String value) {
        return (null == value) ? null : Boolean.valueOf(value);
    }

    /** Helper method to convert Boolean to String.
        @param value Boolean
        @return "true" or "false" or null if value is null
    */
    protected String toString(Boolean value) {
        return (null == value) ? null : (value ? "true" : "false");
    }

    /** Helper method to convert String values to Integer.
        @param value Integer string
        @return Integer value of string or null if string is null
    */
    protected Integer toInteger(String value) {
        return (null == value) ? null : Integer.valueOf(value);
    }

    /** Helper method to convert String values to Long.
        @param value Long string
        @return Long value of string or null if string is null
    */
    protected Long toLong(String value) {
        return (null == value) ? null : Long.valueOf(value);
    }

    /** Helper method to convert Integer to String.
        @param value Integer
        @return value as a String or null if value is null
    */
    protected String toString(Integer value) {
        return (null == value) ? null : value.toString();
    }

    /** Helper method to convert Long to String.
        @param value Long
        @return value as a String or null if value is null
    */
    protected String toString(Long value) {
        return (null == value) ? null : value.toString();
    }

    /** Helper method to convert String values to Date.
        See dateFormat
        @param value Date string
        @return Date value of string or null if string is null
    */
    protected Date toDate(String value) {

        try {
            return (null == value) ? null : dateFormat.parse(value);
        } catch (ParseException e) {
            if (log.isErrorEnabled()) {
                log.error("Error parsing Date (" + value + "): ", e);
            }
        } catch (NullPointerException e) {
            if (log.isErrorEnabled()) {
                log.error("Null found when parsing Date (" + value + "): ", e);
            }
        }

        return null;
    }

    /** Helper method to convert Date to String.
        See dateFormat
        @param value Date
        @return value as a String or null if value is null
    */
    protected String toString(Date value) {
        return (null == value) ? null : dateFormat.format(value);
    }

    /** Get a String of all the standard String fields in a comma separated list of key = value.
        @return Standard string values as a string.
    */
    @Override
    public String toString() {
        String value = "";
        String[] keys = keySet().toArray(new String[0]);

        Arrays.sort(keys);
        for (String key : keys) {
            value += (value.length() == 0 ? "" : ", ") + key + " = " + get(key);
        }

        return value;
    }

}
