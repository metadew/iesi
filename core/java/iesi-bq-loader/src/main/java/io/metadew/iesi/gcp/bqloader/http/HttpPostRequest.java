package io.metadew.iesi.gcp.bqloader.http;

import org.apache.http.client.methods.HttpPost;

public class HttpPostRequest extends HttpRequest {

    public HttpPostRequest(HttpPost httpPost) {
        super(httpPost);
    }
}
