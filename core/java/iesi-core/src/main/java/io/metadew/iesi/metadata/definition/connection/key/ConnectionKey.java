package io.metadew.iesi.metadata.definition.connection.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;

public class ConnectionKey extends MetadataKey {

    private String name;
    private String environment;

    public ConnectionKey(String name, String environment) {
        this.name = name;
        this.environment = environment;
    }

    public String getName() {
        return name;
    }

    public String getEnvironment() {
        return environment;
    }
}
