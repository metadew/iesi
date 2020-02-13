package io.metadew.iesi.metadata.definition.component.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@EqualsAndHashCode(callSuper = false)
@Getter
@RequiredArgsConstructor
public class ComponentKey extends MetadataKey {

    private final String id;
    private final long versionNumber;

}
