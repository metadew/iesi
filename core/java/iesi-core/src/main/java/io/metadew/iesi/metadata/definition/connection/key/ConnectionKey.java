package io.metadew.iesi.metadata.definition.connection.key;

import io.metadew.iesi.metadata.definition.environment.key.EnvironmentKey;
import io.metadew.iesi.metadata.definition.key.MetadataKey;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Builder
public class ConnectionKey extends MetadataKey {

    private final String name;
    private final EnvironmentKey environmentKey;

    public ConnectionKey(String name, String environmentName) {
        this.name = name;
        this.environmentKey = new EnvironmentKey(environmentName);
    }

}
