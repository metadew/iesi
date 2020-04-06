package io.metadew.iesi.metadata.definition.component.key;

import io.metadew.iesi.metadata.definition.environment.key.EnvironmentKey;
import io.metadew.iesi.metadata.definition.key.MetadataKey;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;


@Data
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class ComponentAttributeKey extends MetadataKey {
    private final ComponentKey componentKey;
    private final EnvironmentKey environmentKey;
    private final String componentAttributeName;

}
