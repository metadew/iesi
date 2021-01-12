package io.metadew.iesi.connection.http.request;

import lombok.EqualsAndHashCode;
import org.apache.http.client.methods.HttpPost;

@EqualsAndHashCode(callSuper = true)
public class HttpPostRequest extends HttpRequest {

    public HttpPostRequest(HttpPost httpPost) {
        super(httpPost);
    }
}
