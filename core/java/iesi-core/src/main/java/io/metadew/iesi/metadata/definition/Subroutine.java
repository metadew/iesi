package io.metadew.iesi.metadata.definition;

import java.util.List;

public class Subroutine {

    private String name;
    private String type;
    private String description;
    private List<SubroutineParameter> parameters;

    // Constructors
    public Subroutine() {

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

    public List<SubroutineParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<SubroutineParameter> parameters) {
        this.parameters = parameters;
    }


}