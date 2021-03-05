package io.metadew.iesi.metadata.service.connection.trace.http;

import io.metadew.iesi.connection.http.HttpConnection;
import io.metadew.iesi.metadata.configuration.connection.trace.ConnectionTraceConfiguration;
import io.metadew.iesi.metadata.definition.connection.trace.ConnectionTraceKey;
import io.metadew.iesi.metadata.definition.connection.trace.http.HttpConnectionTrace;
import io.metadew.iesi.script.execution.ActionExecution;
import lombok.extern.log4j.Log4j2;

import java.util.UUID;

@Log4j2
public class HttpConnectionTraceService implements IHttpConnectionTraceService {
    private static final String CONNECTION_TYPE = "http";

    private static HttpConnectionTraceService INSTANCE;

    public synchronized static HttpConnectionTraceService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HttpConnectionTraceService();
        }
        return INSTANCE;
    }

    private HttpConnectionTraceService() {
    }

    public HttpConnectionTrace convert(HttpConnection httpConnection, ActionExecution actionExecution, String actionParameterName) {
        return HttpConnectionTrace.builder()
                .metadataKey(new ConnectionTraceKey(UUID.randomUUID()))
                .runId(actionExecution.getExecutionControl().getRunId())
                .processId(actionExecution.getProcessId())
                .actionParameter(actionParameterName)
                .name(httpConnection.getReferenceName())
                .description(httpConnection.getDescription())
                .type(CONNECTION_TYPE)
                .host(httpConnection.getHost())
                .port(httpConnection.getPort())
                .baseUrl(httpConnection.getBaseUrl())
                .tls(httpConnection.isTls())
                .build();
    }

    public void trace(HttpConnection httpConnection, ActionExecution actionExecution, String actionParameterName) {
        HttpConnectionTrace httpConnectionTrace = convert(httpConnection, actionExecution, actionParameterName);
        ConnectionTraceConfiguration.getInstance().insert(httpConnectionTrace);
    }

}
