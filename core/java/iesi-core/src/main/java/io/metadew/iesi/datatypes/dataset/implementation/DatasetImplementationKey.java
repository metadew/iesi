package io.metadew.iesi.datatypes.dataset.implementation;

import io.metadew.iesi.metadata.definition.key.MetadataKey;
import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Builder
public class DatasetImplementationKey extends MetadataKey {

    private UUID uuid = UUID.randomUUID();

}
