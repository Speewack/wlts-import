/**
 * 
 */
package org.randywebb.wlts;

import java.io.IOException;
import java.util.Properties;

import org.apache.http.auth.AuthenticationException;
import org.apache.http.client.ClientProtocolException;
import org.randywebb.wlts.ldstools.rest.ApiCatalog;
import org.randywebb.wlts.ldstools.rest.LdsToolsClient;
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
	public static void main(String[] args) {
		
		
		try {
			LdsToolsClient client = new LdsToolsClient("someUser","invalidPassword");
		} catch (AuthenticationException e) {
			log.error("Error signing in", e);
		}
		
		//client.signOut();

	}
}
