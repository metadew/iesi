package io.metadew.iesi.metadata.definition;

import java.util.List;

public class Dataset {

    private long id;
    private String name;
    private String type;
    private String description;
    private List<DatasetParameter> parameters;
    private List<DatasetInstance> instances;

    //Constructors
    public Dataset() {

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<DatasetParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<DatasetParameter> parameters) {
        this.parameters = parameters;
    }

    public List<DatasetInstance> getInstances() {
        return instances;
    }

    public void setInstances(List<DatasetInstance> instances) {
        this.instances = instances;
    }


}