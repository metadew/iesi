package io.metadew.iesi.metadata.definition;

import java.util.List;

public class DatasetInstance {

    private long id;
    private String name;
    private String description;
    private List<DatasetInstanceParameter> parameters;
    private List<DatasetInstanceLabel> labels;


    //Constructors
    public DatasetInstance() {

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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<DatasetInstanceParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<DatasetInstanceParameter> parameters) {
        this.parameters = parameters;
    }

    public List<DatasetInstanceLabel> getLabels() {
        return labels;
    }

    public void setLabels(List<DatasetInstanceLabel> labels) {
        this.labels = labels;
    }

}