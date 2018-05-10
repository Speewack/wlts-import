/**
* Application Configuration Object
*/
package org.randywebb.wlts.util;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Loads and caches app properties.
* @author randyw
*
*/
public final class AppConfig extends Properties {

    /** Can be used for logging debugging messages. */
    private static Logger log = LoggerFactory.getLogger(AppConfig.class);

    /** singleton instance. */
    private static AppConfig instance = null;

    /** Get a property as a double.
        @param name The name of the property
        @param defaultValue The default value to use if name has not been set
        @return The value of name as a double, or defaultValue if name has not been set
    */
    public double getProperty(String name, double defaultValue) {
        return Double.parseDouble(getProperty(name, new Double(defaultValue).toString()));
    }

    /** Attempts to load the config.properties file.
            If it fails, this instance has no properties.
    */
    private AppConfig() {
        try {
            this.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties"));
        } catch (IOException e) {
            log.error("Unable to load configuration", e);
        }
    }

    /**
        @return The singleton instance. Creates if it doesn't already exist.
    */
    public static AppConfig getInstance() {
        if (instance == null) {
            instance = new AppConfig();
        }

        return instance;
    }

}
