package io.metadew.iesi.metadata.definition;

import java.util.List;

public class Action {
	
	private long id;
	private long number;
	private String type;
	private String name;
	private String description;
	private String component = "";
	private String condition = "";
	private String iteration = "";
	private String errorExpected = "N";
	private String errorStop = "N";
	private String retries = "";
	private List<ActionParameter> parameters;
	
	//Constructors
	public Action() {
		
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

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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
		return errorExpected;
	}

	public void setErrorExpected(String errorExpected) {
		this.errorExpected = errorExpected;
	}

	public String getErrorStop() {
		return errorStop;
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
		return component;
	}

	public void setComponent(String component) {
		this.component = component;
	}

	public String getIteration() {
		return iteration;
	}

	public void setIteration(String iteration) {
		this.iteration = iteration;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getRetries() {
		return retries;
	}

	public void setRetries(String retries) {
		this.retries = retries;
	}

}