package io.metadew.iesi.metadata.definition;

import java.util.List;

public class ComponentType {

    private String name;
    private String description;
    private List<ComponentTypeParameter> parameters;

    //Constructors
    public ComponentType() {

    }

    //Getters and Setters
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

    public List<ComponentTypeParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<ComponentTypeParameter> parameters) {
        this.parameters = parameters;
    }

}