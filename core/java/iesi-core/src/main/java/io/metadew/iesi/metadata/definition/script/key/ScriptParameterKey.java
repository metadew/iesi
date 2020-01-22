package io.metadew.iesi.metadata.definition.script.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
public class ScriptParameterKey extends MetadataKey {
    private String scriptId;
    private long scriptVersionNumber;
    private String parameterName;

    public ScriptParameterKey(String scriptId, long scriptVersionNumber, String parameterName) {
        this.scriptId = scriptId;
        this.scriptVersionNumber = scriptVersionNumber;
        this.parameterName = parameterName;
    }

    public String getScriptId() {
        return scriptId;
    }

    public long getScriptVersionNumber() {
        return scriptVersionNumber;
    }

    public String getParameterName() {
        return parameterName;
    }
}
