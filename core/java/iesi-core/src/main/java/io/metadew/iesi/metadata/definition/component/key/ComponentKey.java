package io.metadew.iesi.metadata.definition.component.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;
import lombok.*;


@EqualsAndHashCode(callSuper = false)
@Data
@Builder
@AllArgsConstructor
public class ComponentKey extends MetadataKey {

    private final String id;
    private final long versionNumber;

}
