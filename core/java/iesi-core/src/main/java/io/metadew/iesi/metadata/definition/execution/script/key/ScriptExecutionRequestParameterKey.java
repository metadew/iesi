package io.metadew.iesi.metadata.definition.execution.script.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Builder
public class ScriptExecutionRequestParameterKey extends MetadataKey {

    private final String id;

}
