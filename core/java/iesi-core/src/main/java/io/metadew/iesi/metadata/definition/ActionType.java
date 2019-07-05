package io.metadew.iesi.metadata.definition;

import java.util.List;

public class ActionType {

    private String name;
    private String className;
    private String description;
    private List<ActionTypeParameter> parameters;
    private String status = "none";
    
    //Constructors
    public ActionType() {

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

    public List<ActionTypeParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<ActionTypeParameter> parameters) {
        this.parameters = parameters;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}