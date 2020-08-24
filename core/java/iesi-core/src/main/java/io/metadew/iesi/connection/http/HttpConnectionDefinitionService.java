package io.metadew.iesi.connection.http;

import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.ConnectionParameter;

public class HttpConnectionDefinitionService implements IHttpConnectionDefinitionService {

    private static final String CONNECTION_TYPE = "http";
    private static final String HOST_KEY = "host";
    private static final String PORT_KEY = "port";
    private static final String TLS_KEY = "tls";
    private static HttpConnectionDefinitionService INSTANCE;

    public synchronized static HttpConnectionDefinitionService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HttpConnectionDefinitionService();
        }
        return INSTANCE;
    }

    private HttpConnectionDefinitionService() {
    }

    @Override
    public HttpConnectionDefinition convert(Connection connection) {
        if (!(connection.getType().equalsIgnoreCase(CONNECTION_TYPE))) {
            throw new RuntimeException("Cannot convert " + connection.toString() + " to http connection");
        }
        return new HttpConnectionDefinition(
                connection.getMetadataKey().getName(),
                connection.getDescription(),
                connection.getMetadataKey().getEnvironmentKey().getName(),
                connection.getParameters().stream()
                        .filter(componentParameter -> componentParameter.getMetadataKey().getParameterName().equals(HOST_KEY))
                        .findFirst()
                        .map(ConnectionParameter::getValue)
                        .orElseThrow(() -> new RuntimeException("Http component " + connection.toString() + " does not contain a " + HOST_KEY)),
                connection.getParameters().stream()
                        .filter(componentParameter -> componentParameter.getMetadataKey().getParameterName().equals(PORT_KEY))
                        .findFirst()
                        .map(connectionParameter -> Integer.parseInt(connectionParameter.getValue()))
                        .orElse(null),
                connection.getParameters().stream()
                        .filter(componentParameter -> componentParameter.getMetadataKey().getParameterName().equals(HOST_KEY))
                        .findFirst()
                        .map(connectionParameter -> connectionParameter.getValue().equalsIgnoreCase("y"))
                        .orElseThrow(() -> new RuntimeException("Http component " + connection.toString() + " does not contain a " + TLS_KEY)));
    }


}
