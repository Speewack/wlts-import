/**
 * ResponseHandler to parse and process a JSON response
 */
package org.randywebb.wlts.util.http;

import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Handle a JSON response.
    @param <JSONType> This is the type of JSON entity expected to be returned,
                            either JSONObject or JSONArray.
 * @author randyw
 *
 */
public class JSONResponseHandler<JSONType> implements ResponseHandler<JSONType> {

    /** Can be used for logging debugging messages. */
    private static Logger log = LoggerFactory.getLogger(JSONResponseHandler.class);

    /** Converts an HTTP response into a JSON entity.
        @param response The HTTP repsonse
        @return The JSON entity that was returned.
                            If there is a problem parsing the JSON, this may be null.
        @throws ClientProtocolException If it wasn't an HTTP connection,
                                        or we didn't get 200 OK response from the server.
        @throws IOException on network error
    */
    @Override
    public JSONType handleResponse(HttpResponse response)
                                            throws ClientProtocolException, IOException {
        JSONParser parser = new JSONParser();
        JSONType jsonObj = null;

        if (log.isTraceEnabled()) {
            log.trace("Response Status: " + response.getStatusLine().toString());
        }

        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            throw new ClientProtocolException("Unable to retrieve response. Server returned: "
                                                    + response.getStatusLine().toString());
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
