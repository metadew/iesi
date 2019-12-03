package io.metadew.iesi.metadata.definition.script.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;

public class ScriptVersionKey extends MetadataKey {

    private String scriptId;
    private long versionNumber;

    public ScriptVersionKey(String scriptId, long versionNumber) {
        this.scriptId = scriptId;
        this.versionNumber = versionNumber;
    }

    public String getScriptId() {
        return scriptId;
    }

    public long getVersionNumber() {
        return versionNumber;
    }
}
