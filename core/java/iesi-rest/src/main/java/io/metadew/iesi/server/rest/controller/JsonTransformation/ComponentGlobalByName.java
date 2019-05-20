package io.metadew.iesi.server.rest.controller.JsonTransformation;

import java.util.List;
import java.util.stream.Collectors;

import io.metadew.iesi.metadata.definition.Component;
import io.metadew.iesi.metadata.definition.ComponentVersion;

public class ComponentGlobalByName {
	private String type;
	private String name;
	private String description;
	private List<String> versions;

	public ComponentGlobalByName(List<Component> component) {
		super();
		this.type = component.get(0).getType();
		this.name = component.get(0).getName();
		this.description = component.get(0).getDescription();
		this.versions = component.stream().map(components -> components.getVersion().getDescription())
				.collect(Collectors.toList());

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

	public List<String> getVersions() {
		return versions;
	}

}
