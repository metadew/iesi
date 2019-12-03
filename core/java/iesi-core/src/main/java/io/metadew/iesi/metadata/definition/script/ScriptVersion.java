package io.metadew.iesi.metadata.definition.script;

import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.script.key.ScriptVersionKey;

public class ScriptVersion extends Metadata<ScriptVersionKey> {

    private String description = "Default version";

    public ScriptVersion(ScriptVersionKey scriptVersionKey, String description) {
        super(scriptVersionKey);
        this.description = description;
    }

    public ScriptVersion(String scriptId, long number, String description) {
        super(new ScriptVersionKey(scriptId, number));
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
        return getMetadataKey().getVersionNumber();
    }

    public String getScriptId() {
        return getMetadataKey().getScriptId();
    }
}