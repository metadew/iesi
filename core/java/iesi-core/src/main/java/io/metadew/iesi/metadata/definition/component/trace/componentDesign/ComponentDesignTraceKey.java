package io.metadew.iesi.metadata.definition.component.trace;

import io.metadew.iesi.metadata.definition.key.MetadataKey;
import lombok.Data;

import java.util.UUID;

@Data
public class ComponentDesignTraceKey extends MetadataKey {

    private final UUID uuid;
}

