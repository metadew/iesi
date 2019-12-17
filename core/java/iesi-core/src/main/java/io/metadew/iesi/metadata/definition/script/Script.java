package io.metadew.iesi.metadata.definition.script;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.action.Action;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;

import java.util.List;

@JsonDeserialize(using = ScriptJsonComponent.Deserializer.class)
@JsonSerialize(using = ScriptJsonComponent.Serializer.class)
public class Script extends Metadata<ScriptKey> {

    private String name;
    private String description;
    private ScriptVersion version;
    private List<ScriptParameter> parameters;
    private List<Action> actions;


    public Script(String id, String name, String description, ScriptVersion version,
                  List<ScriptParameter> parameters, List<Action> actions) {
        super(new ScriptKey(id));
        this.name = name;
        this.description = description;
        this.version = version;
        this.parameters = parameters;
        this.actions = actions;
    }
    public Script(ScriptKey scriptKey, String name, String description, ScriptVersion version,
                  List<ScriptParameter> parameters, List<Action> actions) {
        super(scriptKey);
        this.name = name;
        this.description = description;
        this.version = version;
        this.parameters = parameters;
        this.actions = actions;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    public List<ScriptParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<ScriptParameter> parameters) {
        this.parameters = parameters;
    }

    public ScriptVersion getVersion() {
        return version;
    }

    public void setVersion(ScriptVersion version) {
        this.version = version;
    }

    public String getId() {
        return getMetadataKey().getScriptId();
    }

    public boolean isEmpty() {
        return (this.name == null || this.name.isEmpty()) ;
    }

}