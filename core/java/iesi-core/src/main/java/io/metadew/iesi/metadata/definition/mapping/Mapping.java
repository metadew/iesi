package io.metadew.iesi.metadata.definition.mapping;

import io.metadew.iesi.metadata.definition.Transformation;

import java.util.List;

public class Mapping {

    private long id;
    private String type = "mapping";
    private String name;
    private String description;
    private MappingVersion version;
    private List<Transformation> transformations;

    public List<Transformation> getTransformations() {
        return transformations;
    }


}