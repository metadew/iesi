package io.metadew.iesi.connection.http.request;

import org.apache.http.client.methods.HttpRequestBase;

/**
 * Object for managing the http request to be used in the http connection object.
 *
 * @author peter.billen
 */
public abstract class HttpRequest {

    private HttpRequestBase httpRequest;

    // Constructor
    public HttpRequest(HttpRequestBase httpRequestBase) {
        this.httpRequest = httpRequestBase;
    }

    public HttpRequestBase getHttpRequest() {
        return httpRequest;
    }

}