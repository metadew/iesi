package io.metadew.iesi.datatypes.dataset.implementation.in.memory;

import io.metadew.iesi.metadata.definition.key.MetadataKey;
import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class InMemoryDatasetImplementationKeyValueKey extends MetadataKey {

    private UUID uuid = UUID.randomUUID();
}
