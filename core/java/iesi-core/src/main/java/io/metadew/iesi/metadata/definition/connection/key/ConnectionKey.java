package io.metadew.iesi.metadata.definition.connection.key;

import io.metadew.iesi.metadata.definition.environment.key.EnvironmentKey;
import io.metadew.iesi.metadata.definition.key.MetadataKey;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
public class ConnectionKey extends MetadataKey {

    private String name;
    private EnvironmentKey environmentKey;

    public ConnectionKey(String name, EnvironmentKey environmentKey) {
        this.name = name;
        this.environmentKey = environmentKey;
    }

    public ConnectionKey(String name, String environmentName) {
        this.name = name;
        this.environmentKey = new EnvironmentKey(environmentName);
    }

    public String getName() {
        return name;
    }

    public EnvironmentKey getEnvironmentKey() {
        return environmentKey;
    }
}
