package io.metadew.iesi.metadata.definition.connection.trace;

import io.metadew.iesi.metadata.definition.key.MetadataKey;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Builder
public class ConnectionTraceKey extends MetadataKey {

    private final UUID uuid;
}

