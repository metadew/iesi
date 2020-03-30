package io.metadew.iesi.server.rest.resource.script.dto;

import io.metadew.iesi.metadata.definition.action.Action;
import io.metadew.iesi.metadata.definition.action.key.ActionKey;
import io.metadew.iesi.metadata.tools.IdentifierTools;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;
import java.util.stream.Collectors;

public class ScriptActionDto extends RepresentationModel<ScriptActionDto> {

    private int retries;
    private String iteration;
    private String condition;
    private long number;
    private String name;
    private String description;
    private String component;
    private boolean errorExpected;
    private boolean errorStop;
    private String type;
    private List<ScriptActionParameterDto>  parameters;

    public ScriptActionDto() {}

    public ScriptActionDto(long number, String name, String type, String description, String component, String condition,
                           String iteration, boolean errorExpected, boolean errorStop, int retries,
                           List<ScriptActionParameterDto> parameters) {
        this.number = number;
        this.name = name;
        this.description = description;
        this.component = component;
        this.condition = condition;
        this.errorExpected = errorExpected;
        this.errorStop = errorStop;
        this.type = type;
        this.parameters = parameters;
        this.retries = retries;
        this.iteration = iteration;
    }

    public Action convertToEntity(String scriptName, long version){
        return new Action(new ActionKey(scriptName, version, IdentifierTools.getActionIdentifier(name)),
                number, type, this.name, description, component, condition, iteration, errorExpected ? "y" : "n",
                errorStop ? "y" : "n", Integer.toString(retries), parameters.stream()
                .map(parameter -> parameter.convertToEntity(scriptName, version, IdentifierTools.getActionIdentifier(name)))
                .collect(Collectors.toList()));

    }

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
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

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public boolean getErrorExpected() {
        return errorExpected;
    }

    public void setErrorExpected(boolean errorExpected) {
        this.errorExpected = errorExpected;
    }

    public boolean getErrorStop() {
        return errorStop;
    }

    public void setErrorStop(boolean errorStop) {
        this.errorStop = errorStop;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<ScriptActionParameterDto> getParameters() {
        return parameters;
    }

    public void setParameters(List<ScriptActionParameterDto> parameters) {
        this.parameters = parameters;
    }

    public int getRetries() {
        return retries;
    }

    public String getIteration() {
        return iteration;
    }

    public String getCondition() {
        return condition;
    }

    public boolean isErrorExpected() {
        return errorExpected;
    }

    public boolean isErrorStop() {
        return errorStop;
    }
}
