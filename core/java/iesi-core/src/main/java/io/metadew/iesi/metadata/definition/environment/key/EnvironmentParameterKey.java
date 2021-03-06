package io.metadew.iesi.metadata.definition.environment.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class EnvironmentParameterKey extends MetadataKey {

    private final EnvironmentKey environmentKey;
    private final String parameterName;

}
