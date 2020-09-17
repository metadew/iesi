package io.metadew.iesi.gcp.connection.http;

import org.apache.http.client.methods.HttpGet;

public class HttpGetRequest extends HttpRequest {

    public HttpGetRequest(HttpGet httpGet) {
        super(httpGet);
    }
}
