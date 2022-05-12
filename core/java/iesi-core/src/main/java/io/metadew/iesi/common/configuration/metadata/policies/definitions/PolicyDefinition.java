package io.metadew.iesi.common.configuration.metadata.policies.definitions;

import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.key.MetadataKey;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class PolicyDefinition<T extends Metadata<? extends MetadataKey>> {
    private String name;

    public abstract void verify(T toVerify);
}
