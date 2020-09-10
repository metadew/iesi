package io.metadew.iesi.metadata.definition.component.trace.componentDesign;

import io.metadew.iesi.metadata.definition.Metadata;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class ComponentDesignTrace extends Metadata<ComponentDesignTraceKey> {

    private final String runId;
    private final Long processId;
    private final String actionParameter;
    private final String componentTypeParameter;
    private final String componentName;
    private final String componentDescription;
    private final Long componentVersion;

    public ComponentDesignTrace(ComponentDesignTraceKey metadataKey, String runId, Long processId, String actionParameter, String componentTypeParameter, String componentName, String componentDescription,
                                Long componentVersion) {
        super(metadataKey);
        this.runId = runId;
        this.processId = processId;
        this.actionParameter = actionParameter;
        this.componentTypeParameter = componentTypeParameter;
        this.componentName = componentName;
        this.componentDescription = componentDescription;
        this.componentVersion = componentVersion;
    }
}
