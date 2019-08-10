package io.metadew.iesi.server.rest.resource.script.dto;

import io.metadew.iesi.metadata.definition.action.Action;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import org.springframework.hateoas.ResourceSupport;

import java.util.List;

public class ScriptActionDto extends ResourceSupport {

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
    private List<ActionParameter>  parameters;

    public ScriptActionDto() {}

    public ScriptActionDto(long number, String name, String type, String description, String component, String condition,
                           String iteration, boolean errorExpected, boolean errorStop, int retries,
                           List<ActionParameter> parameters) {
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

    public Action convertToEntity(){
        return new Action(number, type, name, description, component, condition, iteration, errorExpected, errorStop, retries, parameters);

    }

    public static ScriptActionDto convertToDto(Action action){
        return new ScriptActionDto(action.getNumber(),action.getName(),action.getType(), action.getDescription(),action.getComponent(), action.getCondition(), action.getIteration(), action.getErrorExpected(),action.getErrorStop(),action.getRetries(),action.getParameters());
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

    public List<ActionParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<ActionParameter> parameters) {
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
