package io.metadew.iesi.connection.http;

import io.metadew.iesi.metadata.configuration.connection.ConnectionConfiguration;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;
import io.metadew.iesi.metadata.definition.environment.key.EnvironmentKey;

public class HttpConnectionService implements IHttpConnectionService {

    private static HttpConnectionService INSTANCE;

    public synchronized static HttpConnectionService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HttpConnectionService();
        }
        return INSTANCE;
    }

    private HttpConnectionService() {
    }

    public HttpConnection get(String httpConnectionReferenceName, String environmentReferenceName) {
        Connection connection = ConnectionConfiguration.getInstance().get(new ConnectionKey(httpConnectionReferenceName, new EnvironmentKey(environmentReferenceName)))
                .orElseThrow(() -> new RuntimeException("Could not find definition for http connection " + httpConnectionReferenceName + " for environment " + environmentReferenceName));
        HttpConnectionDefinition httpConnectionDefinition = HttpConnectionDefinitionService.getInstance()
                .convert(connection);
        return convert(httpConnectionDefinition);
    }

    @Override
    public String getBaseUri(HttpConnection httpConnection) {
        return (httpConnection.isTls() ? "https" : "http") + "://" + httpConnection.getHost() + (httpConnection.getPort() != null ? ":" + httpConnection.getPort() : "");
    }


    @Override
    public HttpConnection convert(HttpConnectionDefinition httpConnectionDefinition) {
        return new HttpConnection(
                httpConnectionDefinition.getReferenceName(),
                httpConnectionDefinition.getDescription(),
                httpConnectionDefinition.getEnvironmentReferenceName(),
                httpConnectionDefinition.getHost(),
                httpConnectionDefinition.getPort(),
                httpConnectionDefinition.isTls());
    }

}
