package org.randywebb.wlts.ldstools.json;

import java.util.function.Consumer;

public abstract class AbstractConsumer implements Consumer<Object> {

	/**
	 * Safely convert objects to String, handling Nulls
	 * @param o
	 * @return
	 */
	protected static String convert(Object o)
	{
		return o==null?null:o.toString();
	}

}
