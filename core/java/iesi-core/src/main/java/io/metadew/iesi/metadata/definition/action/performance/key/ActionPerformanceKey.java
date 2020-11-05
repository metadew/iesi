package io.metadew.iesi.metadata.definition.action.performance.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ActionPerformanceKey extends MetadataKey {

    private final String runId;
    @Builder
    public ActionPerformanceKey(String runId, Long procedureId, String scope) {
        this.runId = runId;
        this.procedureId = procedureId;
        this.scope = scope;
    }

    private final Long procedureId;
    private final String scope;

}
