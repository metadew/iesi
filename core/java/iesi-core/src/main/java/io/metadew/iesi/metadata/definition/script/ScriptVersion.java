package io.metadew.iesi.metadata.definition.script;

import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptVersionKey;
import lombok.Builder;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class ScriptVersion extends Metadata<ScriptVersionKey> {

    private String description = "Default version";
    private String createdBy;
    private String createdAt;


    @Builder
    public ScriptVersion(ScriptVersionKey scriptVersionKey, String description, String createdBy, String createdAt) {
        super(scriptVersionKey);
        this.description = description;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    public ScriptVersion(String scriptId, long number, String description, String createdBy, String createdAt) {
        super(new ScriptVersionKey(new ScriptKey(scriptId, number)));
        this.description = description;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
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

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}