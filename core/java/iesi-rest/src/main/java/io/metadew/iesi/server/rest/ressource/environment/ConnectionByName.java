package io.metadew.iesi.server.rest.ressource.environment;
import io.metadew.iesi.metadata.definition.Connection;
import io.metadew.iesi.metadata.definition.ConnectionParameter;

import java.util.List;
import java.util.Objects;

public class ConnectionByName {

	private String name;
	private String type;
	private String description;
	private String environment;
	private List<ConnectionParameter> parameters;

	public ConnectionByName(List<Connection> connection) {
		this.name = connection.get(0).getName();
		this.environment = connection.get(0).getEnvironment();
		this.type = connection.get(0).getType();
		this.description = connection.get(0).getDescription();
		this.parameters = connection.get(0).getParameters();

	}

	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	public List<ConnectionParameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<ConnectionParameter> parameters) {
		this.parameters = parameters;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setDescription(String description) {
		this.description = description;
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