package io.metadew.iesi.metadata.definition.action.design.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ActionParameterDesignTraceKey extends MetadataKey {

    private final String runId;
    private final Long processId;
    private final String actionId;
    private final String name;

}
