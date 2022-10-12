package io.metadew.iesi.connection.http;

import io.metadew.iesi.metadata.configuration.connection.ConnectionConfiguration;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;
import io.metadew.iesi.metadata.definition.environment.key.EnvironmentKey;
import io.metadew.iesi.metadata.service.connection.trace.http.HttpConnectionTraceService;
import io.metadew.iesi.script.execution.ActionExecution;
import org.springframework.stereotype.Service;

@Service
public class HttpConnectionService implements IHttpConnectionService {

    private final ConnectionConfiguration connectionConfiguration;
    private final HttpConnectionTraceService httpConnectionTraceService;

    public HttpConnectionService(ConnectionConfiguration connectionConfiguration, HttpConnectionTraceService httpConnectionTraceService) {
        this.connectionConfiguration = connectionConfiguration;
        this.httpConnectionTraceService = httpConnectionTraceService;
    }

    public HttpConnection get(String httpConnectionReferenceName, ActionExecution actionExecution) {
        Connection connection = connectionConfiguration.get(new ConnectionKey(httpConnectionReferenceName, new EnvironmentKey(actionExecution.getExecutionControl().getEnvName())))
                .orElseThrow(() -> new RuntimeException("Could not find definition for http connection " + httpConnectionReferenceName + " for environment " + actionExecution.getExecutionControl().getEnvName()));
        HttpConnectionDefinition httpConnectionDefinition = HttpConnectionDefinitionService.getInstance()
                .convert(connection);
        return convert(httpConnectionDefinition);
    }

    public HttpConnection getAndTrace(String httpConnectionReferenceName, ActionExecution actionExecution, String actionParameterName) {
        HttpConnection httpConnection = get(httpConnectionReferenceName, actionExecution);
        httpConnectionTraceService.trace(httpConnection, actionExecution, actionParameterName);
        return httpConnection;
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
