/**
 * ResponseHandler to parse and process a JSON response
 */
package org.randywebb.wlts.util.http;

import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author randyw
 *
 */
public class JSONResponseHandler<JSONType> implements ResponseHandler<JSONType> {

	private static Logger log = LoggerFactory.getLogger(JSONResponseHandler.class);

	@Override
	public JSONType handleResponse(HttpResponse response) throws ClientProtocolException, IOException {

		JSONParser parser = new JSONParser();
		JSONType jsonObj = null;

		if (log.isTraceEnabled()) {
			log.trace("Response Status: " + response.getStatusLine().toString());
		}

		if(response.getStatusLine().getStatusCode() != 200)
		{
			throw new ClientProtocolException("Unable to retrieve response. Server returned: " + response.getStatusLine().toString());
		}

		try {
			Object obj = parser.parse(new InputStreamReader(response.getEntity().getContent()));
			jsonObj = (JSONType) obj;

		} catch (UnsupportedOperationException | ParseException e) {
			log.error("Unable to parse JSON response", e);
		}

		return jsonObj;
	}



}
