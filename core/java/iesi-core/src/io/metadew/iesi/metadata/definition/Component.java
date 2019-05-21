package io.metadew.iesi.metadata.definition;

import java.util.List;

public class Component {
	
	private long id;
	private String type;
	private String name;
	private String description;
	private ComponentVersion version;
	private List<ComponentParameter> parameters;
	private List<ComponentAttribute> attributes;
	
	//Constructors
	public Component() {

	}


	public Component(long id, String type, String name, String description, ComponentVersion version,
					 List<ComponentParameter> parameters, List<ComponentAttribute> attributes) {
		this.id = id;
		this.type = type;
		this.name = name;
		this.description = description;
		this.version = version;
		this.parameters = parameters;
		this.attributes = attributes;
	}

	
	//Getters and Setters
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<ComponentParameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<ComponentParameter> parameters) {
		this.parameters = parameters;
	}

	public List<ComponentAttribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<ComponentAttribute> attributes) {
		this.attributes = attributes;
	}

	public ComponentVersion getVersion() {
		return version;
	}

	public void setVersion(ComponentVersion version) {
		this.version = version;
	}

}