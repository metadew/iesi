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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((versions == null) ? 0 : versions.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ComponentGlobalByName other = (ComponentGlobalByName) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (versions == null) {
			if (other.versions != null)
				return false;
		} else if (!versions.equals(other.versions))
			return false;
		return true;
	}

}
