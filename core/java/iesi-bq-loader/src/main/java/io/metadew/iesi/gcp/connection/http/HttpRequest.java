package io.metadew.iesi.gcp.connection.http;

import org.apache.http.client.methods.HttpRequestBase;

public abstract class HttpRequest {

    private HttpRequestBase httpRequest;

    public HttpRequest(HttpRequestBase httpRequestBase) {
        this.httpRequest = httpRequestBase;
    }

    public HttpRequestBase getHttpRequest() {
        return httpRequest;
    }

}