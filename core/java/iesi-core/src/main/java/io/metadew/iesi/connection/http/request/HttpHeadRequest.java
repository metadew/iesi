package io.metadew.iesi.connection.http.request;

import org.apache.http.client.methods.HttpHead;

public class HttpHeadRequest extends HttpRequest {

    public HttpHeadRequest(HttpHead httpHead) {
        super(httpHead);
    }
}
