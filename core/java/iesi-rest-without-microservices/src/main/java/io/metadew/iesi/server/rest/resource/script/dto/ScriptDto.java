package io.metadew.iesi.server.rest.resource.script.dto;

import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.metadata.definition.script.ScriptParameter;
import io.metadew.iesi.metadata.tools.IdentifierTools;
import org.springframework.hateoas.ResourceSupport;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ScriptDto extends ResourceSupport {

    private String name;
    private String description;
    private ScriptVersionDto version;
    private List<ScriptParameter> parameters;
    private List<ScriptActionDto> actions;

    public ScriptDto() {}

    public ScriptDto(String name, String description, ScriptVersionDto version, List<ScriptParameter> parameters, List<ScriptActionDto> actions) {
        this.name = name;
        this.description = description;
        this.version = version;
        this.parameters = parameters;
        this.actions = actions;
    }

    public Script convertToEntity() {
        return new Script(IdentifierTools.getScriptIdentifier(name), name, description,  version.convertToEntity(), parameters,
                actions.stream().map(action -> action.convertToEntity(name, version.getNumber())).collect(Collectors.toList()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ScriptDto)) return false;
        if (!super.equals(o)) return false;
        ScriptDto scriptDto = (ScriptDto) o;
        return getName().equals(scriptDto.getName()) &&
                getDescription().equals(scriptDto.getDescription()) &&
                getVersion().equals(scriptDto.getVersion()) &&
                getParameters().equals(scriptDto.getParameters()) &&
                getActions().equals(scriptDto.getActions());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getName(), getDescription(), getVersion(), getParameters(), getActions());
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

    public List<ScriptActionDto> getActions() {
        return actions;
    }

    public void setActions(List<ScriptActionDto> actions) {
        this.actions = actions;
    }
}
