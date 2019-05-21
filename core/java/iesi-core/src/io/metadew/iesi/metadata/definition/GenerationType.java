package io.metadew.iesi.metadata.definition;

import java.util.List;

public class GenerationType {

    private String name;
    private String description;
    private List<GenerationTypeParameter> parameters;

    //Constructors
    public GenerationType() {

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

    public List<GenerationTypeParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<GenerationTypeParameter> parameters) {
        this.parameters = parameters;
    }


}