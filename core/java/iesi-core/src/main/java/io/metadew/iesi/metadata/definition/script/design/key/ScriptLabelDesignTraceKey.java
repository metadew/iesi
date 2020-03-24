package io.metadew.iesi.metadata.definition.script.design.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptLabelKey;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ScriptLabelDesignTraceKey extends MetadataKey {

    private final String runId;
    private final Long processId;
    private final ScriptLabelKey scriptLabelKey;
}
