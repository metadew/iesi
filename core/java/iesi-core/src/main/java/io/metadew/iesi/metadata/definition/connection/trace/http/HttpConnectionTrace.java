package io.metadew.iesi.metadata.definition.connection.trace.http;

import io.metadew.iesi.metadata.definition.connection.trace.ConnectionTrace;
import io.metadew.iesi.metadata.definition.connection.trace.ConnectionTraceKey;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class HttpConnectionTrace extends ConnectionTrace {

    private final String host;
    private final String baseUrl;
    private final Integer port;
    private final boolean tls;

    @Builder
    public HttpConnectionTrace(ConnectionTraceKey metadataKey, String runId, Long processId, String actionParameter, String name, String type, String description, String host, String baseUrl, Integer port, boolean tls) {
        super(metadataKey, runId, processId, actionParameter, name, type, description);
        this.host = host;
        this.baseUrl = baseUrl;
        this.port = port;
        this.tls = tls;
    }

}
