package io.metadew.iesi.metadata.definition.script.design.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ScriptVersionDesignTraceKey extends MetadataKey {

    private final String runId;
    private final Long processId;

}
