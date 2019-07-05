package io.metadew.iesi.metadata.definition;

import java.util.List;

public class Impersonation {

    private String name;
    private String description;
    private List<ImpersonationParameter> parameters;

    //Constructors
    public Impersonation() {
    }

    public Impersonation(String name, String description, List<ImpersonationParameter> parameters) {
        this.name = name;
        this.description = description;
        this.parameters = parameters;
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

    public List<ImpersonationParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<ImpersonationParameter> parameters) {
        this.parameters = parameters;
    }

}