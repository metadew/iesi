package io.metadew.iesi.metadata.definition.execution.script.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;
import io.metadew.iesi.metadata.tools.IdentifierTools;

public class ScriptExecutionKey extends MetadataKey {

    private final String id;

    public ScriptExecutionKey() {
        this(IdentifierTools.getScriptExecutionRequestIdentifier());
    }

    public ScriptExecutionKey(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

}
