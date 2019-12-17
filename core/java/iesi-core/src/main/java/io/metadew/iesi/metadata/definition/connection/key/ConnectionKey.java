package io.metadew.iesi.metadata.definition.connection.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;

public class ConnectionKey extends MetadataKey {

    private String name;

    public ConnectionKey(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
