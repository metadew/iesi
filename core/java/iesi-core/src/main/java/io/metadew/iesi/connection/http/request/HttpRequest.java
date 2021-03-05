package io.metadew.iesi.connection.http.request;

import lombok.EqualsAndHashCode;
import org.apache.http.client.methods.HttpRequestBase;

@EqualsAndHashCode
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