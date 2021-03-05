package io.metadew.iesi.metadata.definition.component.key;

import io.metadew.iesi.metadata.definition.environment.key.EnvironmentKey;
import io.metadew.iesi.metadata.definition.key.MetadataKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@Builder
public class ComponentAttributeKey extends MetadataKey {
    private final ComponentKey componentKey;
    private final EnvironmentKey environmentKey;
    private final String componentAttributeName;

}
