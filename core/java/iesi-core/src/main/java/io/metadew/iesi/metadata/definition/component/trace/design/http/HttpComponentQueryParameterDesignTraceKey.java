package io.metadew.iesi.metadata.definition.component.trace.design.http;

import io.metadew.iesi.metadata.definition.key.MetadataKey;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
public class HttpComponentQueryParameterDesignTraceKey extends MetadataKey {
    @Builder
    public HttpComponentQueryParameterDesignTraceKey(UUID uuid) {
        this.uuid = uuid;
    }

    private final UUID uuid;
}