package io.metadew.iesi.metadata.definition.component.trace.componentTrace;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class HttpComponentTrace extends ComponentTrace {

    @Builder
    public HttpComponentTrace(ComponentTraceKey metadataKey, String runId, Long processId, String actionParameter, String componentID, String componentTypeParameter, String componentName, Long componentDescription, Long componentVersion, String componentVersionDescription, String connectionName, String type, String endpoint, List<HttpComponentHeader> httpComponentHeader, List<HttpComponentQuery> httpComponentQueries) {
        super(metadataKey, runId, processId, actionParameter, componentID, componentTypeParameter, componentName, componentDescription, componentVersion, componentVersionDescription);
        this.connectionName = connectionName;
        this.type = type;
        this.endpoint = endpoint;
        this.httpComponentHeader = httpComponentHeader;
        this.httpComponentQueries = httpComponentQueries;
    }

    private final String connectionName;
    private final String type;
    private final String endpoint;
    private final List<HttpComponentHeader> httpComponentHeader;
    private final List<HttpComponentQuery> httpComponentQueries;

}
