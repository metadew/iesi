package io.metadew.iesi.metadata.definition.component.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;


@EqualsAndHashCode(callSuper = false)
@Data
//@RequiredArgsConstructor
public class ComponentKey extends MetadataKey {

    private final String id;
    @Builder
    public ComponentKey(String id, long versionNumber) {
        this.id = id;
        this.versionNumber = versionNumber;
    }

    private final long versionNumber;

}
