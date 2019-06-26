package io.metadew.iesi.metadata.definition;

import java.util.List;

public class GenerationRuleType {
	
	private String name;
	private String className;
	private String description;
	private List<GenerationRuleTypeParameter> parameters;
	
	//Constructors
	public GenerationRuleType() {
		
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

	public List<GenerationRuleTypeParameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<GenerationRuleTypeParameter> parameters) {
		this.parameters = parameters;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}
	
}