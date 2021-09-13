package io.metadew.iesi.datatypes.dataset.implementation.database;

import io.metadew.iesi.metadata.definition.key.MetadataKey;
import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Builder
public class DatabaseDatasetImplementationKeyValueKey extends MetadataKey {

    private UUID uuid = UUID.randomUUID();

}