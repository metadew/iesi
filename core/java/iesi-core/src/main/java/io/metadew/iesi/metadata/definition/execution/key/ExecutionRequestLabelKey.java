package io.metadew.iesi.metadata.definition.execution.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ExecutionRequestLabelKey extends MetadataKey {

    private final String id;
}
