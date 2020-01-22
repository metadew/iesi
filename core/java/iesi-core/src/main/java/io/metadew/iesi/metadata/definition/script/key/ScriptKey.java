package io.metadew.iesi.metadata.definition.script.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
public class ScriptKey extends MetadataKey {

    private String scriptId;
    private long scriptVersion;

    public ScriptKey(String scriptId, long scriptVersion) {
        this.scriptId = scriptId;
        this.scriptVersion = scriptVersion;
    }

    public String getScriptId() {
        return scriptId;
    }

    public long getScriptVersion() { return scriptVersion; }
}
