package io.metadew.iesi.metadata.definition.component.trace.componentTrace;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class HttpComponentTrace extends ComponentTrace {

    @Builder
    public HttpComponentTrace(ComponentTraceKey metadataKey, String runId, Long processId, String actionParameter, String componentTypeParameter, String componentName, String componentDescription, Long componentVersion, String connectionName, String type, String endpoint, List<HttpComponentHeaderTrace> httpComponentHeaderTrace, List<HttpComponentQueryParameterTrace> httpComponentQueries) {
        super(metadataKey, runId, processId, actionParameter, componentTypeParameter, componentName, componentDescription, componentVersion);
        this.connectionName = connectionName;
        this.type = type;
        this.endpoint = endpoint;
        this.httpComponentHeaderTrace = httpComponentHeaderTrace;
        this.httpComponentQueries = httpComponentQueries;
    }

    private final String connectionName;
    private final String type;
    private final String endpoint;
    private final List<HttpComponentHeaderTrace> httpComponentHeaderTrace;
    private final List<HttpComponentQueryParameterTrace> httpComponentQueries;

}
