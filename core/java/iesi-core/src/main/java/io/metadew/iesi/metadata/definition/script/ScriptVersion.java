package io.metadew.iesi.metadata.definition.script;

import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptVersionKey;
import lombok.Builder;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class ScriptVersion extends Metadata<ScriptVersionKey> {

    private String description = "Default version";
    private String deleted_At = "NA";

    @Builder
    public ScriptVersion(ScriptVersionKey scriptVersionKey, String description, String deleted_At) {
        super(scriptVersionKey);
        this.description = description;
        this.deleted_At = deleted_At;
    }

    public ScriptVersion(String scriptId, long number, String description, String deleted_At) {
        super(new ScriptVersionKey(new ScriptKey(scriptId, number)));
        this.description = description;
        this.deleted_At = deleted_At;
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

    public String getDeleted_At() { return deleted_At; }

    public void setDeleted_At(String deleted_At) { this.deleted_At = deleted_At; }
}