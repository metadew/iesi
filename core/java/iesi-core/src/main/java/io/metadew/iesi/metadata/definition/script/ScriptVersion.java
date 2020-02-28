package io.metadew.iesi.metadata.definition.script;

import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptVersionKey;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class ScriptVersion extends Metadata<ScriptVersionKey> {

    private String description = "Default version";

    public ScriptVersion(ScriptVersionKey scriptVersionKey, String description) {
        super(scriptVersionKey);
        this.description = description;
    }

    public ScriptVersion(String scriptId, long number, String description) {
        super(new ScriptVersionKey(new ScriptKey(scriptId, number)));
        this.description = description;
    }

    // Getters and Setters
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getNumber() {
        return getMetadataKey().getScriptKey().getScriptVersion();
    }

    public String getScriptId() {
        return getMetadataKey().getScriptKey().getScriptId();
    }
}