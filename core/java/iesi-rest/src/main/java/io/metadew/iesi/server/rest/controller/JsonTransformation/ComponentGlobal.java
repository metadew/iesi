package io.metadew.iesi.server.rest.controller.JsonTransformation;

import io.metadew.iesi.metadata.definition.Component;

public class ComponentGlobal {
	private String type;
	private String name;
	private String description;
	
	public ComponentGlobal(Component component) {
		super();
		this.type = component.getType();
		this.name = component.getName();
		this.description = component.getDescription();
	}

	public String getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}
	
}
