package io.metadew.iesi.metadata.definition;

import java.util.List;

public class ScriptType {

    private String name;
    private String description;
    private List<ScriptTypeParameter> parameters;

    //Constructors
    public ScriptType() {

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

    public List<ScriptTypeParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<ScriptTypeParameter> parameters) {
        this.parameters = parameters;
    }


}