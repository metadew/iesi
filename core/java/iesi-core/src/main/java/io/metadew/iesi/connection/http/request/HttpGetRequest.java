package io.metadew.iesi.connection.http.request;

import lombok.EqualsAndHashCode;
import org.apache.http.client.methods.HttpGet;

@EqualsAndHashCode(callSuper = true)
public class HttpGetRequest extends HttpRequest {

    public HttpGetRequest(HttpGet httpGet) {
        super(httpGet);
    }
}
