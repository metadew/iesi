package io.metadew.iesi.connection.http.request;

import org.apache.http.client.methods.HttpOptions;

public class HttpOptionsRequest extends HttpRequest {

    public HttpOptionsRequest(HttpOptions httpOptions) {
        super(httpOptions);
    }
}
