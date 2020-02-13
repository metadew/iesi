package io.metadew.iesi.metadata.definition.execution.script.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ScriptExecutionKey extends MetadataKey {

    private final String id;

}
