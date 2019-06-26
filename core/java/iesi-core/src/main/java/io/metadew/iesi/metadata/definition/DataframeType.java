package io.metadew.iesi.metadata.definition;

import java.util.List;

public class DataframeType {

    private String name;
    private String description;
    private List<DataframeTypeParameter> parameters;

    //Constructors
    public DataframeType() {

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

    public List<DataframeTypeParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<DataframeTypeParameter> parameters) {
        this.parameters = parameters;
    }


}