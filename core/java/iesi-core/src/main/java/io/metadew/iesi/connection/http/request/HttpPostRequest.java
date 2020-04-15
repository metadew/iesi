package io.metadew.iesi.connection.http.request;

import org.apache.http.client.methods.HttpPost;

public class HttpPostRequest extends HttpRequest {

    public HttpPostRequest(HttpPost httpPost) {
        super(httpPost);
    }
}
