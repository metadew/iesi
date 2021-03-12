package io.metadew.iesi.metadata.definition.component.trace;

import io.metadew.iesi.metadata.definition.key.MetadataKey;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
public class ComponentTraceKey extends MetadataKey {
    @Builder
    public ComponentTraceKey(UUID uuid) {
        this.uuid = uuid;
    }

    private final UUID uuid;
}

