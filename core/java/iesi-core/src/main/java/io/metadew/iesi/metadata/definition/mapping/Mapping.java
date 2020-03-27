package io.metadew.iesi.metadata.definition.mapping;

import io.metadew.iesi.metadata.definition.Transformation;
import lombok.Data;

import java.util.List;

@Data
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