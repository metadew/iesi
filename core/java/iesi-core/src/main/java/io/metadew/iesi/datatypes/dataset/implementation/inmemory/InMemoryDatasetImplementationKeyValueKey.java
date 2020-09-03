package io.metadew.iesi.datatypes.dataset.implementation.inmemory;

import io.metadew.iesi.metadata.definition.key.MetadataKey;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class InMemoryDatasetImplementationKeyValueKey extends MetadataKey {

    private UUID uuid = UUID.randomUUID();

}