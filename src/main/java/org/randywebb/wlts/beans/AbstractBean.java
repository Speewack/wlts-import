package org.randywebb.wlts.beans;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractBean extends HashMap<String,String> {

	private static Logger log = LoggerFactory.getLogger(AbstractBean.class);

	private List<String> booleanFields;
	private List<String> dateFields;
	private List<String> integerFields;
	private SimpleDateFormat dateFormat;

	public AbstractBean() {
		this.booleanFields = new ArrayList<String>();
		this.dateFields = new ArrayList<String>();
		this.integerFields = new ArrayList<String>();
	}

	public AbstractBean(SimpleDateFormat dateFormat, List<String> booleanFields, List<String> integerFields, List<String> dateFields) {
		this.dateFormat = dateFormat;
		this.booleanFields = null == booleanFields ? new ArrayList<String>() : booleanFields;
		this.dateFields = null == dateFields ? new ArrayList<String>() : dateFields;
		this.integerFields = null == integerFields ? new ArrayList<String>() : integerFields;
	}

	protected static <T> List<T> fromArray(JSONArray array, List<T> toFill, Class<T> clazz) {

		if (null != array) {
			try {
				Constructor<T> constructor = clazz.getConstructor(JSONObject.class);

				for (Object o : array) {
					try {
						toFill.add(constructor.newInstance(new Object[] {(JSONObject)o}));
					} catch(InvocationTargetException e) {
						log.error("Cannot invoke " + clazz.getName() + "(JSONObject)");
					} catch(IllegalAccessException e) {
						log.error("No permissions to create " + clazz.getName() + "(JSONObject)");
					} catch(InstantiationException e) {
						log.error("Unable to create " + clazz.getName() + "(JSONObject)");
					}
				}

			} catch(NoSuchMethodException e) {
				log.error("No constructor " + clazz.getName() + "(JSONObject)");
			}
		}

		return toFill;
	}

	public AbstractBean update(JSONObject definition, String[] expectedKeys) {

		for (String key : expectedKeys) {
			setFromJSON(definition, key);
		}

		return this;
	}

	public Map<String,Object> forCSV() {
		HashMap<String,Object> results = new HashMap<String,Object>();

		for (Map.Entry<String,String> entry : entrySet()) {
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

	protected void setFromJSON(JSONObject definition, String key) {
		Object value = definition.get(key);

		if ( (null != value) || !containsKey(key) ) {
			put(key, (null == value) ? null : value.toString());
		}

	}

  protected Boolean toBoolean(String value) {
   	return (null == value) ? null : Boolean.valueOf(value);
  }

  protected String toString(Boolean value) {
   	return (null == value) ? null : (value ? "true" : "false");
  }

  protected Integer toInteger(String value) {
   	return (null == value) ? null : Integer.valueOf(value);
  }

  protected String toString(Integer value) {
   	return (null == value) ? null : value.toString();
  }

  protected Date toDate(String value) {

	try {
		return (null == value) ? null : dateFormat.parse(value);
	} catch(ParseException e) {
        log.error("Error parsing Date (" + value + "): ", e);
	} catch(NullPointerException e) {
        log.error("Null found when parsing Date (" + value + "): ", e);
	}

   	return null;
  }

  protected String toString(Date value) {
   	return (null == value) ? null : dateFormat.format(value);
  }

	@Override
	public String toString() {
		String value = "";

		for (HashMap.Entry<String,String> entry : entrySet()) {
			value += (value.length() == 0 ? "" : ", ") + entry.getKey() + " = " + entry.getValue();
		}

		return value;
	}

}
