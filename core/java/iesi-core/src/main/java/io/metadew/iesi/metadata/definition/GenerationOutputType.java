package io.metadew.iesi.metadata.definition;

import java.util.List;

public class GenerationOutputType {

    private String name;
    private String description;
    private List<GenerationOutputTypeParameter> parameters;

    //Constructors
    public GenerationOutputType() {

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

    public List<GenerationOutputTypeParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<GenerationOutputTypeParameter> parameters) {
        this.parameters = parameters;
    }


}