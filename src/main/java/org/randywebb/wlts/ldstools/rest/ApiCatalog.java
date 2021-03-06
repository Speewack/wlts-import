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
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.randywebb.wlts.util.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** List of URL endpoints and settings.
 * @author randyw
 *
 */
public final class ApiCatalog extends Properties {

    /** Can be used for logging debugging messages. */
    private static Logger log = LoggerFactory.getLogger(ApiCatalog.class);

    /** Singleton for the API Catalog. */
    private static ApiCatalog instance = null;

    /** Construct the only instance of the ApiCatalog. */
    private ApiCatalog() {
        HttpGet httpGet = new HttpGet(AppConfig.getInstance().getProperty("lds-tools-config"));
        CloseableHttpResponse apiCatalogResponse;
        try {
            CloseableHttpClient httpclient = LdsToolsClient.getHttpClient();
            apiCatalogResponse = httpclient.execute(httpGet);

            try {
                HttpEntity catalogEntity = apiCatalogResponse.getEntity();
                // do something useful with the response body
                // and ensure it is fully consumed

                convertCatalog(catalogEntity.getContent());

                EntityUtils.consume(catalogEntity);
            } finally {
                apiCatalogResponse.close();
            }
        } catch (IOException e) {
            log.error("Unable to retrieve API Catalog", e);
        }
    }

    /**
     * Retrieve an instance of the LDS Tools API Catalog.
     *
     * @return ApiCatalog
     */
    public static ApiCatalog getInstance() {
        if (instance == null) {
            instance = new ApiCatalog();
        }
        return instance;
    }

    /**
     * Convert the JSON API catalog from lds tools to a Properties object.
     *
     * @param in
     *            InputStream containing the JSON catalog from LDS-Tools API
     */
    private void convertCatalog(InputStream in) {

        try {
            Object obj = new JSONParser().parse(new InputStreamReader(in));
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
