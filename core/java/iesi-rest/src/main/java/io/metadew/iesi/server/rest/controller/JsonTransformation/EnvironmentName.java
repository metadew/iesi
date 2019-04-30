package io.metadew.iesi.server.rest.controller.JsonTransformation;

import java.util.List;

import io.metadew.iesi.metadata.definition.Connection;

public class EnvironmentName {

	private String name;
	private String type;
	private String description;

	public EnvironmentName(List<Connection> connection) {
		this.name = connection.get(0).getName();
		this.type = connection.get(0).getType();
		this.description = connection.get(0).getDescription();
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public String getDescription() {
		return description;
	}



}