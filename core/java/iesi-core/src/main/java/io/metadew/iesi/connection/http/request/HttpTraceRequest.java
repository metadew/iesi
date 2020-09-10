package io.metadew.iesi.connection.http.request;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpTrace;

public class HttpTraceRequest extends HttpRequest {

    public HttpTraceRequest(HttpTrace httpTrace) {
        super(httpTrace);
    }
}
