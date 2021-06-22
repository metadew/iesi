package io.metadew.iesi.metadata.definition.script;

import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptVersionKey;
import lombok.Builder;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class ScriptVersion extends Metadata<ScriptVersionKey> {

    private String description = "Default version";
    private String lastModifiedBy;
    private String lastModifiedAt;


    @Builder
    public ScriptVersion(ScriptVersionKey scriptVersionKey, String description, String lastModifiedBy, String lastModifiedAt) {
        super(scriptVersionKey);
        this.description = description;
        this.lastModifiedBy = lastModifiedBy;
        this.lastModifiedAt = lastModifiedAt;
    }

    public ScriptVersion(String scriptId, long number, String description, String lastModifiedBy, String lastModifiedAt) {
        super(new ScriptVersionKey(new ScriptKey(scriptId, number)));
        this.description = description;
        this.lastModifiedBy = lastModifiedBy;
        this.lastModifiedAt = lastModifiedAt;
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

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public String getLastModifiedAt() {
        return lastModifiedAt;
    }

    public void setLastModifiedAt(String lastModifiedAt) {
        this.lastModifiedAt = lastModifiedAt;
    }

}