package io.metadew.iesi.metadata.definition.action.trace.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ActionTraceKey extends MetadataKey {

    private final String runId;
    @Builder
    public ActionTraceKey(String runId, Long processId, String actionId) {
        this.runId = runId;
        this.processId = processId;
        this.actionId = actionId;
    }

    private final Long processId;
    private final String actionId;

}
