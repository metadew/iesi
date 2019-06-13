package io.metadew.iesi.server.rest.ressource.script;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import io.metadew.iesi.metadata.definition.*;
import org.springframework.hateoas.ResourceSupport;

public class ScriptDto extends ResourceSupport {

    private String name;
    private String description;
    private ScriptVersionDto version;
    private List<ScriptParameter> parameters;
    private ScriptActionDto actions;

    public ScriptDto(String name, String description, ScriptVersionDto version, List<ScriptParameter> parameters, ScriptActionDto actions) {
        this.name = name;
        this.description = description;
        this.version = version;
        this.parameters = parameters;
        this.actions = actions;
    }

    public Script convertToEntity() {
        return new Script(null, null, name, description,  version.convertToEntity(),  parameters, actions.convertToEntity());
    }

    public static ScriptDto convertToDto(Script script) {
        return new ScriptDto(script.getType(), script.getName(), ScriptVersionDto.convertToDto(script.getVersion()), script.getParameters(), ScriptActionDto.convertToDto(script.getActions()));
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

    public ScriptVersionDto getVersion() {
        return version;
    }

    public void setVersion(ScriptVersionDto version) {
        this.version = version;
    }

    public List<ScriptParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<ScriptParameter> parameters) {
        this.parameters = parameters;
    }

    public ScriptActionDto getActions() {
        return actions;
    }

    public void setActions(ScriptActionDto actions) {
        this.actions = actions;
    }
}
