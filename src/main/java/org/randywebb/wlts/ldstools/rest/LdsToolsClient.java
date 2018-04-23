/**
 *
 */
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
import org.json.simple.JSONObject;
import org.randywebb.wlts.util.AppConfig;
import org.randywebb.wlts.util.http.JSONResponseHandler;
import org.randywebb.wlts.util.http.NoOpResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author randyw
 *
 */
public class LdsToolsClient {

	private static Logger log = LoggerFactory.getLogger(LdsToolsClient.class);

	private static CloseableHttpClient _httpclient = null;
	// private static BasicCookieStore cookieStore = new BasicCookieStore();

	private Properties apiCatalog = ApiCatalog.getInstance();
	private Properties appConfig = AppConfig.getInstance();
	private String unitNumber = null;

	public LdsToolsClient(String user, String password) throws AuthenticationException {
		getHttpClient();
		signIn(user, password);
		unitNumber = getUnitNumber();
	}

	/**
	 * Deliberately package scoped to enable other ldstools api components
	 * access to a common client
	 *
	 * @return
	 */
	static CloseableHttpClient getHttpClient() {
		if (_httpclient == null) {
			_httpclient = HttpClients.createDefault();

			// _httpclient = HttpClients.custom()
			// .setDefaultCookieStore(cookieStore)
			// .build();
		}
		return _httpclient;
	}

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
	 */
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

	/**
	 * Assumes user is signed in to LDS Tools API
	 * Retrieves the signed-in user's unit number
	 *
	 * @return String containing the unit number
	 */
	private String getUnitNumber()
	{
		String unitNumber = null;

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
		return unitNumber;
	}

	public JSONObject getEndpointInfo(String endpointName) throws IOException
	{
		HttpGet httpGet = new HttpGet(apiCatalog.getProperty(endpointName));

		return getHttpClient().execute(httpGet, new JSONResponseHandler());
	}

	public InputStream getAppProperty(String appPropertyName) throws IOException
	{
		HttpGet httpGet = new HttpGet(AppConfig.getInstance().getProperty(appPropertyName));
		HttpResponse response = getHttpClient().execute(httpGet);
		return response.getEntity().getContent();
	}

	public InputStream getMemberInfo() throws IOException
	{
		return getAppProperty("mls-report-endpoint");
	}

}
