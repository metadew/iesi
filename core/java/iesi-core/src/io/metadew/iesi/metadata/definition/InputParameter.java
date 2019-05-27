package io.metadew.iesi.metadata.definition;


public class InputParameter {

    private String name;
    private String scriptName;
    private String actionName;
    private boolean actionInputParameter;
    private String actionParameterName;

    //Constructors
    public InputParameter() {

    }

    //Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScriptName() {
        return scriptName;
    }

    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public boolean isActionInputParameter() {
        return actionInputParameter;
    }

    public void setActionInputParameter(boolean actionInputParameter) {
        this.actionInputParameter = actionInputParameter;
    }

    public String getActionParameterName() {
        return actionParameterName;
    }

    public void setActionParameterName(String actionParameterName) {
        this.actionParameterName = actionParameterName;
    }


}