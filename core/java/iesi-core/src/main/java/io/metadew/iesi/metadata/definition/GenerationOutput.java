package io.metadew.iesi.metadata.definition;

import java.util.List;

public class GenerationOutput {

    private long id;
    private String name;
    private String type = "";
    private String description;
    private List<GenerationOutputParameter> parameters;

    // Constructors
    public GenerationOutput() {

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

    public List<GenerationOutputParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<GenerationOutputParameter> parameters) {
        this.parameters = parameters;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


}