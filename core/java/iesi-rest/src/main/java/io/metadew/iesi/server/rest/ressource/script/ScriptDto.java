package io.metadew.iesi.server.rest.ressource.script;

import io.metadew.iesi.metadata.definition.Action;
import io.metadew.iesi.metadata.definition.Script;
import io.metadew.iesi.metadata.definition.ScriptParameter;
import io.metadew.iesi.metadata.definition.ScriptVersion;
import org.springframework.hateoas.ResourceSupport;

import java.util.List;

public class ScriptDto extends ResourceSupport {

    private String name;
    private  String description;
    private  ScriptVersion version;
    private  List<ScriptParameter> parameters;
    private  List<Action> actions;

    public ScriptDto() {}

    public ScriptDto(String name, String description, ScriptVersion version, List<ScriptParameter> parameters, List<Action> actions) {
        this.name = name;
        this.description = description;
        this.version = version;
        this.parameters = parameters;
        this.actions = actions;
    }

    public ScriptVersion getVersion() {
        return version;
    }

    public void setVersion(ScriptVersion version) {
        this.version = version;
    }

    public Script convertToEntity() { return new Script(name, description, description, version, parameters, actions);
    }

    public static ScriptDto convertToDto(Script script) {
        return new ScriptDto(script.getName(),  script.getDescription(), script.getVersion(), script.getParameters()
        , script.getActions());
    }

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

    public List<ScriptParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<ScriptParameter> parameters) {
        this.parameters = parameters;
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }
}
