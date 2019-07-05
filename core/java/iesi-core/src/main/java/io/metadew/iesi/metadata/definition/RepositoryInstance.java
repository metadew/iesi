package io.metadew.iesi.metadata.definition;

import java.util.List;

public class RepositoryInstance {

    private long id;
    private String name;
    private String description;
    private List<RepositoryInstanceParameter> parameters;
    private List<RepositoryInstanceLabel> labels;


    //Constructors
    public RepositoryInstance() {

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

    public List<RepositoryInstanceParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<RepositoryInstanceParameter> parameters) {
        this.parameters = parameters;
    }

    public List<RepositoryInstanceLabel> getLabels() {
        return labels;
    }

    public void setLabels(List<RepositoryInstanceLabel> labels) {
        this.labels = labels;
    }


}