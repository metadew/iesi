package io.metadew.iesi.server.rest.controller.JsonTransformation;

import java.util.HashMap;
import java.util.List;

import io.metadew.iesi.metadata.definition.Component;
import io.metadew.iesi.metadata.definition.ComponentAttribute;
import io.metadew.iesi.metadata.definition.ComponentParameter;

public class ComponentPost {

	private String type;
	private String name;
	private String description;
	private HashMap<String, Object> versions;
	private List<ComponentParameter> parameters;
	private List<ComponentAttribute> attributes;

	public ComponentPost(List<Component> component) {
		super();
		this.type = component.get(0).getType();
		this.name = component.get(0).getName();
		this.description = component.get(0).getDescription();
		this.versions = hashversions(component);
		this.parameters = component.get(0).getParameters();
		this.attributes = component.get(0).getAttributes();
	}

	private HashMap<String, Object> hashversions(List<Component> component) {
		HashMap<String, Object> newVersions = new HashMap<String, Object>();
		for (Component comp : component) {
			Long number = comp.getVersion().getNumber();
			String description = comp.getVersion().getDescription();
			newVersions.put("number", number);
			newVersions.put("description", description);
		}
		return newVersions;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public HashMap<String, Object> getVersions() {
		return versions;
	}

	public void setVersions(HashMap<String, Object> versions) {
		this.versions = versions;
	}

	public List<ComponentParameter> getParameters() {
		return parameters;
	}

	public List<ComponentAttribute> getAttributes() {
		return attributes;
	}

}
