package io.metadew.iesi.metadata.definition.execution.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;

public class ExecutionRequestKey extends MetadataKey {

    private final String id;

    public ExecutionRequestKey(String id) {
        this.id =id;
    }

    public String getId() {
        return id;
    }
}
