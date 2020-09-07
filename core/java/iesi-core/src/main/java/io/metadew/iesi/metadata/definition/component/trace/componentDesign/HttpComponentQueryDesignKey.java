package io.metadew.iesi.metadata.definition.component.trace.componentDesign;

import io.metadew.iesi.metadata.definition.key.MetadataKey;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
public class HttpComponentQueryDesignKey extends MetadataKey {
    @Builder
    public HttpComponentQueryDesignKey(UUID uuid) {
        this.uuid = uuid;
    }

    private final UUID uuid;
}