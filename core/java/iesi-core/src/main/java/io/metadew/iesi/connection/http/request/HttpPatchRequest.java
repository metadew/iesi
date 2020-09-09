package io.metadew.iesi.connection.http.request;

import org.apache.http.client.methods.HttpPatch;

public class HttpPatchRequest extends HttpRequest {

    public HttpPatchRequest(HttpPatch httpPatch) {
        super(httpPatch);
    }
}
