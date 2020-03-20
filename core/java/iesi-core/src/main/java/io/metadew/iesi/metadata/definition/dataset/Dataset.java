package io.metadew.iesi.metadata.definition.dataset;

import java.util.List;

public class Dataset {

    private long id;
    private String name;
    private String type;
    private String description;
    private List<DatasetParameter> parameters;
    private List<DatasetInstance> instances;

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setParameters(List<DatasetParameter> parameters) {
        this.parameters = parameters;
    }


}