package org.randywebb.wlts.beans;

import java.util.HashMap;
import java.util.List;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractBean extends HashMap<String,String> {

	private static Logger log = LoggerFactory.getLogger(AbstractBean.class);

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

	protected void setFromJSON(JSONObject definition, String key) {
		Object value = definition.get(key);

		if ( (null != value) || !containsKey(key) ) {
			put(key, (null == value) ? null : value.toString());
		}

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
