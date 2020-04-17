package io.metadew.iesi.gcp.bqloader.http;

import org.apache.http.client.methods.HttpGet;

public class HttpGetRequest extends HttpRequest {

    public HttpGetRequest(HttpGet httpGet) {
        super(httpGet);
    }
}
