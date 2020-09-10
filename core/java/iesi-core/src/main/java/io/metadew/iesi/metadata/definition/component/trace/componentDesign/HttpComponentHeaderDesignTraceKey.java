package io.metadew.iesi.metadata.definition.component.trace.componentDesign;

import io.metadew.iesi.metadata.definition.key.MetadataKey;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
public class HttpComponentHeaderDesignTraceKey extends MetadataKey {

    @Builder
    public HttpComponentHeaderDesignTraceKey(UUID uuid) {
        this.uuid = uuid;
    }

    private final UUID uuid;
}