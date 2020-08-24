package io.metadew.iesi.connection.http;

import io.metadew.iesi.metadata.configuration.connection.ConnectionConfiguration;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;
import io.metadew.iesi.metadata.definition.environment.key.EnvironmentKey;
import io.metadew.iesi.script.execution.ActionExecution;

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

    public HttpConnection get(String httpConnectionReferenceName, ActionExecution actionExecution) {
        // TODO: trace design and trace
        Connection connection = ConnectionConfiguration.getInstance().get(new ConnectionKey(httpConnectionReferenceName, new EnvironmentKey(actionExecution.getExecutionControl().getEnvName())))
                .orElseThrow(() -> new RuntimeException("Could not find definition for http connection " + httpConnectionReferenceName + " for environment " + actionExecution));
        HttpConnectionDefinition httpConnectionDefinition = HttpConnectionDefinitionService.getInstance()
                .convert(connection);
        return convert(httpConnectionDefinition);
    }

    @Override
    public String getBaseUri(HttpConnection httpConnection) {
        return (httpConnection.isTls() ? "https" : "http") + "://" +
                httpConnection.getHost() +
                (httpConnection.getPort() != null ? ":" + httpConnection.getPort() : "") +
                (httpConnection.getBaseUrl() != null ? "/" + httpConnection.getBaseUrl() : "");
    }


    @Override
    public HttpConnection convert(HttpConnectionDefinition httpConnectionDefinition) {
        return new HttpConnection(
                httpConnectionDefinition.getReferenceName(),
                httpConnectionDefinition.getDescription(),
                httpConnectionDefinition.getEnvironmentReferenceName(),
                httpConnectionDefinition.getHost(),
                httpConnectionDefinition.getBaseUrl(),
                httpConnectionDefinition.getPort(),
                httpConnectionDefinition.isTls());
    }

}
