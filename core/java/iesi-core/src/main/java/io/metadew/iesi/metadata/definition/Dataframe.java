package io.metadew.iesi.metadata.definition;

import java.util.List;

public class Dataframe {

    private long id;
    private String type;
    private String name;
    private String description;
    private DataframeVersion version;
    private List<DataframeParameter> parameters;
    private List<DataframeItem> items;

    // Constructors
    public Dataframe() {

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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public DataframeVersion getVersion() {
        return version;
    }

    public void setVersion(DataframeVersion version) {
        this.version = version;
    }

    public List<DataframeParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<DataframeParameter> parameters) {
        this.parameters = parameters;
    }

    public List<DataframeItem> getItems() {
        return items;
    }

    public void setItems(List<DataframeItem> items) {
        this.items = items;
    }


}