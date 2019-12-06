package io.metadew.iesi.metadata.definition.script.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;

public class ScriptKey extends MetadataKey {

    private String scriptId;

    public ScriptKey(String scriptId) {
        this.scriptId = scriptId;
    }

    public String getScriptId() {
        return scriptId;
    }
}
