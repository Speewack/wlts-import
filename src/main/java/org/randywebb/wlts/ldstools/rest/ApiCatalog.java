/**
 * 
 */
package org.randywebb.wlts.ldstools.rest;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.randywebb.wlts.util.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author randyw
 *
 */
public class ApiCatalog extends Properties {

	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(ApiCatalog.class);

	JSONParser parser = new JSONParser();

	private static ApiCatalog _instance = null;

	/**
	 * 
	 */
	private ApiCatalog() {
		HttpGet httpGet = new HttpGet(AppConfig.getInstance().getProperty("lds-tools-config"));
		CloseableHttpResponse apiCatalogResponse;
		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			apiCatalogResponse = httpclient.execute(httpGet);
			// The underlying HTTP connection is still held by the response
			// object
			// to allow the response content to be streamed directly from the
			// network socket.
			// In order to ensure correct deallocation of system resources
			// the user MUST call CloseableHttpResponse#close() from a finally
			// clause.
			// Please note that if response content is not fully consumed the
			// underlying
			// connection cannot be safely re-used and will be shut down and
			// discarded
			// by the connection manager.
			try {
				HttpEntity catalogEntity = apiCatalogResponse.getEntity();
				// do something useful with the response body
				// and ensure it is fully consumed

				convertCatalog(catalogEntity.getContent());

				EntityUtils.consume(catalogEntity);
			} finally {
				apiCatalogResponse.close();
				httpclient.close();
			}
		} catch (IOException e) {
			log.error("Unable to retrieve API Catalog", e);
		}
	}

	/**
	 * Retrieve an instance of the LDS Tools API Catalog
	 * 
	 * @return ApiCatalog
	 */
	public static ApiCatalog getInstance() {
		if (_instance == null) {
			_instance = new ApiCatalog();
		}
		return _instance;
	}

	/**
	 * Convert the JSON API catalog from lds tools to a Properties object
	 * 
	 * @param in
	 *            InputStream containing the JSON catalog from LDS-Tools API
	 * @return
	 */
	private void convertCatalog(InputStream in) {

		try {
			Object obj = parser.parse(new InputStreamReader(in));
			JSONObject jsonObject = (JSONObject) obj;

			Set<?> keySet = jsonObject.keySet();

			for (Object key : keySet) {
				setProperty(key.toString(), jsonObject.get(key).toString());
			}

		} catch (IOException | ParseException e) {
			log.error("Unable to parse JSON Catalog", e);
		}
	}

}
