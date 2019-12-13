package io.metadew.iesi.metadata.definition.connection;

import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;

import java.util.List;

public class Connection extends Metadata<ConnectionKey> {

    private String name;
    private String type;
    private String description;
    private String environment;
    List<ConnectionParameter> parameters;

    public Connection(String name, String type, String description, String environment, List<ConnectionParameter> parameters) {
        super(new ConnectionKey(name, environment));
        this.type = type;
        this.description = description;
        this.parameters = parameters;
    }

    // Constructors
    public Connection(ConnectionKey connectionKey, String type, String description, List<ConnectionParameter> parameters) {
        super(connectionKey);
        this.type = type;
        this.description = description;
        this.parameters = parameters;
    }

    public List<ConnectionParameter> getParameters() {
        return parameters;
    }

    // Getters and Setters
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

	public boolean isEmpty() {
		return (this.name == null || this.name.isEmpty()) ;
	}

}