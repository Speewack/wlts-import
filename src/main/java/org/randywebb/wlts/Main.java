/**
 * 
 */
package org.randywebb.wlts;

import org.randywebb.wlts.util.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author randyw
 *
 */
public class Main {

	private static Logger log = LoggerFactory.getLogger(Main.class);
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		AppConfig config = AppConfig.getInstance();
		
		for (Object key : config.keySet()) {
			System.out.println(key.toString() + " - " + config.getProperty((String)key));
		}

	}

}
