package io.metadew.iesi.metadata.definition.execution.script.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;

public class ScriptExecutionRequestKey extends MetadataKey {

    private final String id;


    public ScriptExecutionRequestKey(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

}
