package io.metadew.iesi.metadata.definition.component.trace.componentTrace;

import io.metadew.iesi.metadata.definition.Metadata;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class ComponentTrace extends Metadata<ComponentTraceKey> {

    private final String runId;
    private final Long processId;

    public ComponentTrace(ComponentTraceKey metadataKey, String runId, Long processId, String actionParameter, String componentID, String componentTypeParameter, String componentName, Long componentDescription, Long componentVersion, String componentVersionDescription) {
        super(metadataKey);
        this.runId = runId;
        this.processId = processId;
        this.actionParameter = actionParameter;
        this.componentID = componentID;
        this.componentTypeParameter = componentTypeParameter;
        this.componentName = componentName;
        this.componentDescription = componentDescription;
        this.componentVersion = componentVersion;
        this.componentVersionDescription = componentVersionDescription;
    }

    private final String actionParameter;
    private final String componentID;
    private final String componentTypeParameter;
    private final String componentName;
    private final Long componentDescription;
    private final Long componentVersion;
    private final String componentVersionDescription;

}
