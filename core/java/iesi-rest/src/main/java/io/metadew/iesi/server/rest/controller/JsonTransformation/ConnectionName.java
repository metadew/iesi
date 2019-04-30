package io.metadew.iesi.server.rest.controller.JsonTransformation;

import java.util.List;
import java.util.stream.Collectors;

import io.metadew.iesi.metadata.definition.Connection;

public class ConnectionName {
	private String name;
	private String type;
	private String description;
	private List<String> environments;


	public ConnectionName(String name, String type, String description, List<String> environments) {
		this.name = name;
		this.type = type;
		this.description = description;
		this.environments = environments;
	}
	
	public ConnectionName(List<Connection> connections) {
		this.name = connections.get(0).getName();
		this.type = connections.get(0).getType();
		this.description = connections.get(0).getDescription();
		System.out.println("Connections size: " + connections.size());
		this.environments = connections.stream()
				.map(connection -> connection.getEnvironment())
				.collect(Collectors.toList());
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

	public List<String> getEnvironments() {
		return environments;
	}
}
