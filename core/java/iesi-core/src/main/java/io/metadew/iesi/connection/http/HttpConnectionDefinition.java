package io.metadew.iesi.connection.http;

import lombok.Data;

@Data
public class HttpConnectionDefinition {

    private final String referenceName;
    private final String description;
    private final String environmentReferenceName;
    private final String host;
    private final int port;
    private final boolean tls;
}
