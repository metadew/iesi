package io.metadew.iesi.metadata.definition.component.trace.componentDesign;

import io.metadew.iesi.metadata.definition.Metadata;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class ComponentDesignTrace extends Metadata<ComponentDesignTraceKey> {

    private final String runId;
    private final Long processId;
    private final String actionParameter;
    private final String componentTypeParameter;
    private final String componentName;
    private final String componentDescription;
    private final String componentVersion;
    private final List<HttpComponentDesignTrace> httpComponentDesignTraces;
    private final List<HttpComponentHeaderDesign> httpComponentHeaderDesigns;
    private final List<HttpComponentQueryDesign> httpComponentQueryDesigns;

    public ComponentDesignTrace(ComponentDesignTraceKey metadataKey, String runId, Long processId, String actionParameter, String componentTypeParameter, String componentName, String componentDescription, String componentVersion, List<HttpComponentDesignTrace> httpComponentDesignTraces, List<HttpComponentHeaderDesign> httpComponentHeaderDesigns, List<HttpComponentQueryDesign> httpComponentQueryDesigns) {
        super(metadataKey);
        this.runId = runId;
        this.processId = processId;
        this.actionParameter = actionParameter;
        this.componentTypeParameter = componentTypeParameter;
        this.componentName = componentName;
        this.componentDescription = componentDescription;
        this.componentVersion = componentVersion;
        this.httpComponentDesignTraces = httpComponentDesignTraces;
        this.httpComponentHeaderDesigns = httpComponentHeaderDesigns;
        this.httpComponentQueryDesigns = httpComponentQueryDesigns;
    }
}
