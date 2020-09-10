package io.metadew.iesi.metadata.definition.component.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;


@EqualsAndHashCode(callSuper = false)
@Data
@Builder
@RequiredArgsConstructor
public class ComponentKey extends MetadataKey {

    private final String id;
    private final long versionNumber;

}
