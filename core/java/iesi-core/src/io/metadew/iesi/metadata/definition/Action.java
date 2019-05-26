package io.metadew.iesi.metadata.definition;

import io.metadew.iesi.metadata.tools.IdentifierTools;

import java.util.List;

public class Action {

    private String id;
    private long number;
    private String type;
    private String name;
    private String description;
    private String component;
    private String condition;
    private String iteration;
    private String errorExpected;
    private String errorStop;
    private String retries;
    private List<ActionParameter> parameters;

    //Constructors
    public Action() {

    }

    // TODO: make optional Paramaters of type Optional instead of ""

    public Action(String id, long number, String type, String name, String description, String component,
                  String condition, String iteration, String errorExpected, String errorStop, String retries, List<ActionParameter> parameters) {
        this.id = id;
        this.number = number;
        this.type = type;
        this.name = name;
        this.description = description;
        this.component = component;
        this.condition = condition;
        this.iteration = iteration;
        this.errorExpected = errorExpected;
        this.errorStop = errorStop;
        this.retries = retries;
        this.parameters = parameters;
    }

    public Action(long number, String type, String name, String description, String component,
                  String condition, String iteration, String errorExpected, String errorStop, String retries, List<ActionParameter> parameters) {
        this.id = IdentifierTools.getActionIdentifier(name);
        this.number = number;
        this.type = type;
        this.name = name;
        this.description = description;
        this.component = component;
        this.condition = condition;
        this.iteration = iteration;
        this.errorExpected = errorExpected;
        this.errorStop = errorStop;
        this.retries = retries;
        this.parameters = parameters;
    }

    //Getters and Setters
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

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getErrorExpected() {
        return errorExpected == null? "N" : errorExpected;
    }

    public void setErrorExpected(String errorExpected) {
        this.errorExpected = errorExpected;
    }

    public String getErrorStop() {
        return errorStop == null? "N" : errorStop;
    }

    public void setErrorStop(String errorStop) {
        this.errorStop = errorStop;
    }

    public List<ActionParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<ActionParameter> parameters) {
        this.parameters = parameters;
    }

    public String getComponent() {
        return component == null? "" : component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getIteration() {
        return iteration == null? "" : iteration;
    }

    public void setIteration(String iteration) {
        this.iteration = iteration;
    }

    public String getCondition() {
        return condition == null? "" : condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getRetries() {
        return retries == null? "" : retries;
    }

    public void setRetries(String retries) {
        this.retries = retries;
    }

    public String getId() {
        if (id == null) this.id = IdentifierTools.getActionIdentifier(this.getName());
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}