package io.metadew.iesi.metadata.definition.action;

import io.metadew.iesi.metadata.tools.IdentifierTools;

import java.util.ArrayList;
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
    private int retries = 0;
    private List<ActionParameter> parameters = new ArrayList<>();

    //Constructors
    public Action() {

    }

    // TODO: make optional Paramaters of type Optional instead of ""

    public Action(String id, long number, String type, String name, String description, String component,
                  String condition, String iteration, boolean errorExpected, boolean errorStop, int retries, List<ActionParameter> parameters) {
        this.id = id;
        this.number = number;
        this.type = type;
        this.name = name;
        this.description = description;
        this.component = component;
        this.condition = condition;
        this.iteration = iteration;
        this.errorExpected = errorExpected ? "Y" : "N";
        this.errorStop = errorStop ? "Y" : "N";
        this.retries = retries;
        this.parameters = parameters;
    }
    public Action(String id, long number, String type, String name, String description, String component,
                  String condition, String iteration, String errorExpected, String errorStop, String retries, List<ActionParameter> parameters) {
        this(id, number, type, name, description, component, condition, iteration, errorExpected.equalsIgnoreCase("y"),
                errorStop.equalsIgnoreCase("y"), retries==null? 0:Integer.parseInt(retries), parameters);
    }

    public Action(long number, String type, String name, String description, String component,
                  String condition, String iteration, String errorExpected, String errorStop, String retries, List<ActionParameter> parameters) {
        this(number, type, name, description, component, condition, iteration, errorExpected.equalsIgnoreCase("y"),
                errorStop.equalsIgnoreCase("y"), retries==null? 0:Integer.parseInt(retries), parameters);
    }

    public Action(long number, String type, String name, String description, String component,
                  String condition, String iteration, boolean errorExpected, boolean errorStop, int retries, List<ActionParameter> parameters) {
        this(IdentifierTools.getActionIdentifier(name), number, type, name, description, component, condition, iteration,
                errorExpected, errorStop, retries, parameters);
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

    public boolean getErrorExpected() {
        return errorExpected.equalsIgnoreCase("y");
    }

    public void setErrorExpected(String errorExpected) {
        this.errorExpected = errorExpected;
    }

    public boolean getErrorStop() {
        return errorStop.equalsIgnoreCase("y");
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

    public int getRetries() {
        return retries;
    }

    public void setRetries(int retries) {
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