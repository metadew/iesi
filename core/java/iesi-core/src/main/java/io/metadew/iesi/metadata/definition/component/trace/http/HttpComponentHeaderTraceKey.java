package io.metadew.iesi.metadata.definition.component.trace.http;

import io.metadew.iesi.metadata.definition.key.MetadataKey;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
public class HttpComponentHeaderTraceKey extends MetadataKey {

    @Builder
    public HttpComponentHeaderTraceKey(UUID uuid) {
        this.uuid = uuid;
    }

    private final UUID uuid;
}
