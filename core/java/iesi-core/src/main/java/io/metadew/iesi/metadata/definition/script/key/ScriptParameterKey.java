package io.metadew.iesi.metadata.definition.script.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ScriptParameterKey extends MetadataKey {
    private final ScriptKey scriptKey;
    private final String parameterName;

    public ScriptParameterKey(String scriptId, long scriptVersionNumber, String parameterName) {
        this.scriptKey = new ScriptKey(scriptId, scriptVersionNumber);
        this.parameterName = parameterName;
    }



}
