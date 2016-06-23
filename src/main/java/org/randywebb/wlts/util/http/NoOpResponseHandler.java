package org.randywebb.wlts.util.http;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;

public class NoOpResponseHandler implements ResponseHandler <StatusLine> {

	/* (non-Javadoc)
	 * @see org.apache.http.client.ResponseHandler#handleResponse(org.apache.http.HttpResponse)
	 */
	@Override
	public StatusLine handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
		StatusLine status = response.getStatusLine();
		return status;
	}

}
