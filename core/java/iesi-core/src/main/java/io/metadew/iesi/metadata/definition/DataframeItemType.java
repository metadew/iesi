package io.metadew.iesi.metadata.definition;

import java.util.List;

public class DataframeItemType {

    private String name;
    private String description;
    private List<DataframeItemTypeParameter> parameters;

    //Constructors
    public DataframeItemType() {

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

    public List<DataframeItemTypeParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<DataframeItemTypeParameter> parameters) {
        this.parameters = parameters;
    }

}