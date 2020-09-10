package io.metadew.iesi.metadata.definition.action.performance.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ActionPerformanceKey extends MetadataKey {

    private final String runId;
    private final Long procedureId;
    private final String scope;

}
