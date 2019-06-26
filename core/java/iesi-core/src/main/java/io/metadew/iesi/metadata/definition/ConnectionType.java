package io.metadew.iesi.metadata.definition;

import java.util.List;

public class ConnectionType {

    private String name;
    private String description;
    private List<ConnectionTypeParameter> parameters;

    // Constructors
    public ConnectionType() {

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

    public List<ConnectionTypeParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<ConnectionTypeParameter> parameters) {
        this.parameters = parameters;
    }

}