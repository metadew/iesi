package io.metadew.iesi.metadata.definition.script;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.action.Action;
import io.metadew.iesi.metadata.definition.script.key.ScriptVersionKey;
import lombok.*;

import java.util.List;
import java.util.Set;

@JsonDeserialize(using = ScriptJsonComponent.Deserializer.class)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Getter
@Setter
public class ScriptVersion extends Metadata<ScriptVersionKey> {

    private String description = "Default version";
    private String createdBy;
    private String createdAt;
    private String lastModifiedBy;
    private String lastModifiedAt;
    private Script script;
    private Set<ScriptParameter> parameters;
    private Set<Action> actions;
    private Set<ScriptLabel> labels;

    @Builder
    public ScriptVersion(ScriptVersionKey scriptVersionKey, Script script, String description, Set<ScriptParameter> parameters,
                         Set<Action> actions, Set<ScriptLabel> labels, String createdBy, String createdAt,
                         String lastModifiedBy, String lastModifiedAt) {
        super(scriptVersionKey);
        this.description = description;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.lastModifiedBy = lastModifiedBy;
        this.lastModifiedAt = lastModifiedAt;
        this.script = script;
        this.parameters = parameters;
        this.actions = actions;
        this.labels = labels;
    }


    public long getNumber() {
        return getMetadataKey().getScriptVersion();
    }

    public String getScriptId() {
        return getMetadataKey().getScriptKey().getScriptId();
    }

}