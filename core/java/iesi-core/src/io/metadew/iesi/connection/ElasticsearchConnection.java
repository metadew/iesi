package io.metadew.iesi.connection;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * Connection object for Elasticsearch.
 *
 * @author peter.billen
 */
public class ElasticsearchConnection {

    private String connectionURL;

    // Constructor
    public ElasticsearchConnection() {
        super();
    }

    public ElasticsearchConnection(String connectionUrl) {
        super();
        this.setConnectionURL(connectionUrl);
    }

    // Methods
    public void putStringEntity(String input, String index, String identifier) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPut httpPut = new HttpPut(
                "http://localhost:9200/" + index + "/_doc/" + identifier);
        try {
            httpPut.setEntity(new StringEntity(input));

            httpPut.setHeader("Content-Type", "application/json");

            // Create a custom response handler
            ResponseHandler<String> responseHandler = response -> {
                int status = response.getStatusLine().getStatusCode();
                if (status >= 200 && status < 300) {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity) : null;
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }
            };
            httpclient.execute(httpPut, responseHandler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Getters and setters
    public String getConnectionURL() {
        return connectionURL;
    }

    public void setConnectionURL(String connectionURL) {
        this.connectionURL = connectionURL;
    }
}