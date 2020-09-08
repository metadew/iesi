package io.metadew.iesi.datatypes.dataset;

import io.metadew.iesi.metadata.definition.key.MetadataKey;
import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Builder
public class DatasetKey extends MetadataKey {

    private UUID uuid = UUID.randomUUID();

}
