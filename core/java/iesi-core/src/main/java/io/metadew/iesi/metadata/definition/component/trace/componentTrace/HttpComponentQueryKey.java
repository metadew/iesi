package io.metadew.iesi.metadata.definition.component.trace.componentTrace;

import io.metadew.iesi.metadata.definition.key.MetadataKey;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
public class HttpComponentQueryKey extends MetadataKey {

    @Builder
    public HttpComponentQueryKey(UUID uuid) {
        this.uuid = uuid;
    }

    private final UUID uuid;
}