package io.metadew.iesi.metadata.definition.script;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.action.Action;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@JsonDeserialize(using = ScriptJsonComponent.Deserializer.class)
@JsonSerialize(using = ScriptJsonComponent.Serializer.class)
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Script extends Metadata<ScriptKey> {

    private String name;
    private String description;
    private ScriptVersion version;
    private List<ScriptParameter> parameters;
    private List<Action> actions;
    private List<ScriptLabel> labels;

    @Builder
    public Script(ScriptKey scriptKey, String name, String description, ScriptVersion version,
                  List<ScriptParameter> parameters, List<Action> actions, List<ScriptLabel> labels) {
        super(scriptKey);
        this.name = name;
        this.description = description;
        this.version = version;
        this.parameters = parameters;
        this.actions = actions;
        this.labels = labels;
    }

}