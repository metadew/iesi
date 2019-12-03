package io.metadew.iesi.metadata.definition.script.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;

public class ScriptKey extends MetadataKey {

    private String scriptId;
    private long scriptVersionNumber;

    public ScriptKey(String scriptId, long scriptVersionNumber) {
        this.scriptId = scriptId;
        this.scriptVersionNumber = scriptVersionNumber;
    }

    public String getScriptId() {
        return scriptId;
    }

    public long getScriptVersionNumber() {
        return scriptVersionNumber;
    }
}
