package io.metadew.iesi.metadata.definition.environment.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;

public class EnvironmentKey extends MetadataKey {
    private String name;

    public EnvironmentKey(String name){
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
