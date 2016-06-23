/**
 * 
 */
package org.randywebb.wlts;

import java.io.IOException;
import java.util.Properties;

import org.apache.http.client.ClientProtocolException;
import org.randywebb.wlts.ldstools.rest.ApiCatalog;
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
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public static void main(String[] args) throws ClientProtocolException, IOException {
		
		Properties apiCatalog = ApiCatalog.getInstance();
		
		for (Object key : apiCatalog.keySet())
		{
			System.out.println(key.toString() + " - " + apiCatalog.getProperty(key.toString()));
		}
		
	}
	

}
