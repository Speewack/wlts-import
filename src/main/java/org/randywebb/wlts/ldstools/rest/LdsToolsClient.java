package org.randywebb.wlts.ldstools.rest;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import org.randywebb.wlts.util.AppConfig;
import org.randywebb.wlts.util.http.JSONResponseHandler;
import org.randywebb.wlts.util.http.NoOpResponseHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Maintains the credentials for a connection to LDS sites.
 * @author randyw
 *
 */
public class LdsToolsClient {

	/** Can be used for logging debugging messages */
	private static Logger log = LoggerFactory.getLogger(LdsToolsClient.class);

	/** Singleton instance */
	private static CloseableHttpClient _httpclient = null;
	// private static BasicCookieStore cookieStore = new BasicCookieStore();

	/** The api catalog which lists standard lds endpoints */
	private Properties apiCatalog = ApiCatalog.getInstance();
	/** cache unit number */
	private String unitNumber = null;

	/** Create a logged in instance of LDS tools client.
		@param user The LDS Tools username
		@param password The LDS tools password
		@throws AuthenticationException In the event username or password is incorrect
	*/
	public LdsToolsClient(String user, String password) throws AuthenticationException {
		getHttpClient();
		signIn(user, password);
	}

	/**
	 * Deliberately package scoped to enable other ldstools api components
	 * access to a common client
	 *
	 * @return A cached http client
	 */
	public static CloseableHttpClient getHttpClient() {
		if (_httpclient == null) {
			_httpclient = HttpClients.createDefault();

			// _httpclient = HttpClients.custom()
			// .setDefaultCookieStore(cookieStore)
			// .build();
		}
		return _httpclient;
	}

	/** Signs in to LDS tools.
		@param user LDS authentication username
		@param password LDS tools password
		@throws AuthenticationException In the event username or password is incorrect
	*/
	private void signIn(String user, String password) throws AuthenticationException {

		try {
			HttpPost httpPost = new HttpPost(apiCatalog.getProperty("auth-url"));

			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("username", user));
			nvps.add(new BasicNameValuePair("password", password));
			httpPost.setEntity(new UrlEncodedFormEntity(nvps));

			StatusLine status = getHttpClient().execute(httpPost, new NoOpResponseHandler());
			if (log.isTraceEnabled()) {
				log.trace("Sign-in Status: " + status.toString());
			}

			if (status.getStatusCode() != 200) {
				throw new AuthenticationException("Unable to sign in to LDS Tools API");
			}
		} catch (IOException e) {
			log.error("Error signing in to LDS Tools API", e);
		}
	}

	/**
	 * Signs out of the lds.org Session
	 * /
	private void signOut() {
		log.trace("Signing out of LDS Tools API");
		HttpGet httpGet = new HttpGet(apiCatalog.getProperty("signout-url"));
		try {
			StatusLine status = getHttpClient().execute(httpGet, new NoOpResponseHandler());
			if (log.isTraceEnabled()) {
				log.trace("Sign-out Status: " + status.toString());
			}

		} catch (IOException e) {
			log.error("Error signing out of LDS Tools API", e);
		}

	}
    */

	/** Are leader reports enabled.
		Checks the API catalog to determine if they are available
		@return If leader reports are enabled.
	*/
	public boolean leaderReportsAvailable()
	{
		return apiCatalog.getProperty("leader-reports-enabled").substring(0,1).equalsIgnoreCase("t");
	}

	/**
	 * Assumes user is signed in to LDS Tools API
	 * Retrieves the signed-in user's unit number
	 *
	 * @return String containing the unit number
	 */
	public String getUnitNumber()
	{
		if (null == unitNumber) {
			try {
				JSONObject jsonObj = getEndpointInfo("current-user-unit");

				unitNumber =  jsonObj.get("message").toString();
				if (log.isTraceEnabled())
				{
					log.trace("Unit Number Found: " + unitNumber);
				}

			} catch (IOException e) {
				log.error("Error retrieving Unit Number", e);
			}
		}
		return unitNumber;
	}

	/** Get the contents of an endpoint from the catalog.
		@param endpointName The key from the catalog for the endpoint url
		@param args If endpoint url contains formatting characters. %@ is converted to %s
		@return The contents from accessing the url as a JSON object
		@throws IOException on io errors
	*/
	public JSONObject getEndpointInfo(String endpointName, String... args) throws IOException
	{
		String url = apiCatalog.getProperty(endpointName).replace("%@", "%s");
		HttpGet httpGet = new HttpGet(String.format(url, args));

		return getHttpClient().execute(httpGet, new JSONResponseHandler<JSONObject>());
	}

	/** Get the contents of an endpoint from the catalog.
		@param endpointName The key from the catalog for the endpoint url
		@param args If endpoint url contains formatting characters. %@ is converted to %s
		@return The contents from accessing the url as a JSON array
		@throws IOException on io errors
	*/
	public JSONArray getEndpointList(String endpointName, String... args) throws IOException
	{
		String url = apiCatalog.getProperty(endpointName).replace("%@", "%s");
		HttpGet httpGet = new HttpGet(String.format(url, args));

		return getHttpClient().execute(httpGet, new JSONResponseHandler<JSONArray>());
	}

	/** Get an input stream to a URL in the app properties.
		@param appPropertyName The name of the app property
		@param args If endpoint url contains formatting characters. %@ is converted to %s
		@return The stream to the endpoint referenced by the app property.
		@throws IOException on io errors
	*/
	public InputStream getAppProperty(String appPropertyName, String... args) throws IOException
	{
		String url = AppConfig.getInstance().getProperty(appPropertyName).replace("%@", "%s");
		HttpGet httpGet = new HttpGet(String.format(url, args));
		HttpResponse response = getHttpClient().execute(httpGet);
		return response.getEntity().getContent();
	}

	/** Get an input stream to a URL in the app properties.
		@param appPropertyName The name of the app property
		@param args If endpoint url contains formatting characters. %@ is converted to %s
		@return The contents from accessing the url as a JSON object
		@throws IOException on io errors
	*/
	public JSONObject getAppPropertyEndpointInfo(String appPropertyName, String... args) throws IOException
	{
		String url = AppConfig.getInstance().getProperty(appPropertyName).replace("%@", "%s");
		HttpGet httpGet = new HttpGet(String.format(url, args));

		return getHttpClient().execute(httpGet, new JSONResponseHandler<JSONObject>());
	}

	/** Get an input stream to a URL in the app properties.
		@param appPropertyName The name of the app property
		@param args If endpoint url contains formatting characters. %@ is converted to %s
		@return The contents from accessing the url as a JSON array
		@throws IOException on io errors
	*/
	public JSONArray getAppPropertyEndpointList(String appPropertyName, String... args) throws IOException
	{
		String url = AppConfig.getInstance().getProperty(appPropertyName).replace("%@", "%s");
		HttpGet httpGet = new HttpGet(String.format(url, args));

		return getHttpClient().execute(httpGet, new JSONResponseHandler<JSONArray>());
	}

	/** Get the mls-report-endpoint contents stream.
		@return The stream for mls report
		@throws IOException on io errors
	*/
	public InputStream getMemberInfo() throws IOException
	{
		return getAppProperty("mls-report-endpoint");
	}

}
