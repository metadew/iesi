package io.metadew.iesi.metadata.definition.script.trace.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ScriptParameterTraceKey extends MetadataKey {

    private final String runId;
    private final Long processId;
    private final String scriptParameterName;

}
