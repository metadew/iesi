package io.metadew.iesi.metadata.definition;

import java.util.List;

public class Repository {

    private long id;
    private String name;
    private String type;
    private String description;
    private List<RepositoryParameter> parameters;
    private List<RepositoryInstance> instances;

    //Constructors
    public Repository() {

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

    public List<RepositoryParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<RepositoryParameter> parameters) {
        this.parameters = parameters;
    }

    public List<RepositoryInstance> getInstances() {
        return instances;
    }

    public void setInstances(List<RepositoryInstance> instances) {
        this.instances = instances;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


}