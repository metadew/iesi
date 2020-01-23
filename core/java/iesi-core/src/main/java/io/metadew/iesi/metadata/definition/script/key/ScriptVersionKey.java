package io.metadew.iesi.metadata.definition.script.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
public class ScriptVersionKey extends MetadataKey {

    private final ScriptKey scriptKey;

    public ScriptVersionKey(String scriptId, long versionNumber) {
        this.scriptKey = new ScriptKey(scriptId, versionNumber);
    }

    public ScriptVersionKey(ScriptKey scriptKey) {
        this.scriptKey = scriptKey;
    }

    public ScriptKey getScriptKey() {
        return scriptKey;
    }
}
