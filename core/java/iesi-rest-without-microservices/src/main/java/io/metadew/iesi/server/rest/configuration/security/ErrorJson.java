package io.metadew.iesi.server.rest.configuration.security;

import java.util.Map;

public class ErrorJson {

    public final Integer status;
    public final String error;
    public final String message;
    public final String timestamp;
    public final String trace;

    public ErrorJson(int status, Map<String, Object> errorAttributes) {
        this.status = status;
        this.error = (String) errorAttributes.get("error");
        this.message = (String) errorAttributes.get("message");
        this.timestamp = errorAttributes.get("timestamp").toString();
        this.trace = (String) errorAttributes.get("trace");
    }

}