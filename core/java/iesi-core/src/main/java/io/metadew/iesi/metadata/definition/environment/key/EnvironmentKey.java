package io.metadew.iesi.metadata.definition.environment.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class EnvironmentKey extends MetadataKey {
    private final String name;

    @Builder
    public EnvironmentKey(String name) {
        this.name = name;
    }
}