package io.metadew.iesi.metadata.definition.script.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;
import io.metadew.iesi.metadata.definition.script.Script;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
public class ScriptParameterKey extends MetadataKey {
    private final ScriptKey scriptKey;
    private String parameterName;

    public ScriptParameterKey(String scriptId, long scriptVersionNumber, String parameterName) {
        this.scriptKey = new ScriptKey(scriptId, scriptVersionNumber);
        this.parameterName = parameterName;
    }

    public ScriptParameterKey(ScriptKey scriptKey, String parameterName) {
        this.scriptKey = scriptKey;
        this.parameterName = parameterName;
    }

    public String getParameterName() {
        return parameterName;
    }

    public ScriptKey getScriptKey() {
        return scriptKey;
    }
}
