package io.metadew.iesi.metadata.definition.component.trace.componentDesign;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class HttpComponentDesignTrace extends ComponentDesignTrace {

    @Builder
    public HttpComponentDesignTrace(ComponentDesignTraceKey metadataKey, String runId, Long processId,
                                    String actionParameter, String componentTypeParameter, String componentName, String componentDescription,
                                    Long componentVersion, String connectionName, String type, String endpoint, List<HttpComponentHeaderDesign> httpComponentHeaderDesigns, List<HttpComponentQueryDesign> httpComponentQueryDesigns) {
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

    private List<HttpComponentHeaderDesign> httpComponentHeaderDesigns;
    private List<HttpComponentQueryDesign> httpComponentQueryDesigns;

}
