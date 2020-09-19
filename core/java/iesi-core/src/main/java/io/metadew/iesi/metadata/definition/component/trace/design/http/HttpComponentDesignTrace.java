package io.metadew.iesi.metadata.definition.component.trace.design.http;

import io.metadew.iesi.metadata.definition.component.trace.design.ComponentDesignTrace;
import io.metadew.iesi.metadata.definition.component.trace.design.ComponentDesignTraceKey;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class HttpComponentDesignTrace extends ComponentDesignTrace {

    @Builder
    public HttpComponentDesignTrace(ComponentDesignTraceKey metadataKey, String runId, Long processId,
                                    String actionParameter, String componentTypeParameter, String componentName, String componentDescription,
                                    Long componentVersion, String connectionName, String type, String endpoint, List<HttpComponentHeaderDesignTrace> httpComponentHeaderDesigns, List<HttpComponentQueryParameterDesignTrace> httpComponentQueryDesigns) {
        super(metadataKey, runId, processId, actionParameter, componentTypeParameter, componentName, componentDescription, componentVersion);
        this.connectionName = connectionName;
        this.type = type;
        this.endpoint = endpoint;
        this.httpComponentHeaderDesigns = httpComponentHeaderDesigns;
        this.httpComponentQueryDesigns = httpComponentQueryDesigns;
    }

    private final String connectionName;
    private final String type;
    private final String endpoint;

    private List<HttpComponentHeaderDesignTrace> httpComponentHeaderDesigns;
    private List<HttpComponentQueryParameterDesignTrace> httpComponentQueryDesigns;

}
