/**
 * Application Configuration Object
 */
package org.randywebb.wlts.util;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author randyw
 *
 */
public class AppConfig extends Properties {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(AppConfig.class);
	
	private static AppConfig _instance = null;

	private AppConfig(){
		try {
			this.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties"));
		} catch (IOException e) {
			log.error("Unable to load configuration", e);
		}
	}
	
	public static AppConfig getInstance() {
		if(_instance==null)
		{
			_instance = new AppConfig();
		}
		return _instance;
	}
}
