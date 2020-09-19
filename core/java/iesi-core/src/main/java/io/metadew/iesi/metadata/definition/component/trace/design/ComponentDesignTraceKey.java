package io.metadew.iesi.metadata.definition.component.trace.design;

import io.metadew.iesi.metadata.definition.key.MetadataKey;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
public class ComponentDesignTraceKey extends MetadataKey {
    @Builder
    public ComponentDesignTraceKey(UUID uuid) {
        this.uuid = uuid;
    }

    private final UUID uuid;
}

