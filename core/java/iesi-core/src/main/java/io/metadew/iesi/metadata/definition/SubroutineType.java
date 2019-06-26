package io.metadew.iesi.metadata.definition;

import java.util.List;

public class SubroutineType {

    private String name;
    private String description;
    private List<SubroutineTypeParameter> parameters;

    // Constructors
    public SubroutineType() {

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

    public List<SubroutineTypeParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<SubroutineTypeParameter> parameters) {
        this.parameters = parameters;
    }

}