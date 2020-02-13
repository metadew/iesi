package io.metadew.iesi.metadata.definition.script.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ScriptVersionKey extends MetadataKey {

    private final ScriptKey scriptKey;

    public ScriptVersionKey(String scriptId, long versionNumber) {
        this.scriptKey = new ScriptKey(scriptId, versionNumber);
    }

}
