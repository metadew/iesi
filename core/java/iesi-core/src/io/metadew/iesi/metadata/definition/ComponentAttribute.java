package io.metadew.iesi.metadata.definition;


public class ComponentAttribute {
	
	private String name;
	private String environment;
	private String value;
	
	//Constructors
	public ComponentAttribute() {
		
	}

	//Getters and Setters
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}

}