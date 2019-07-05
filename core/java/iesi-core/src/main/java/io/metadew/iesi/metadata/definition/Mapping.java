package io.metadew.iesi.metadata.definition;

import java.util.List;

public class Mapping {

    private long id;
    private String type = "mapping";
    private String name;
    private String description;
    private MappingVersion version;
    private List<Transformation> transformations;

    // Constructors
    public Mapping() {

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public MappingVersion getVersion() {
        return version;
    }

    public void setVersion(MappingVersion version) {
        this.version = version;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Transformation> getTransformations() {
        return transformations;
    }

    public void setTransformations(List<Transformation> transformations) {
        this.transformations = transformations;
    }


}