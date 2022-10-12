package io.metadew.iesi.connection.http;

import lombok.Data;

@Data
public class HttpConnection {

    private final String referenceName;
    private final String description;
    private final String environmentReferenceName;
    private final String host;
    private final String baseUrl;
    private final Integer port;
    private final boolean tls;
}
