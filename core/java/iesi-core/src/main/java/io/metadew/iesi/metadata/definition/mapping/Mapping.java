package io.metadew.iesi.metadata.definition.mapping;

import io.metadew.iesi.metadata.definition.Transformation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mapping {

    private long id;
    private String type = "mapping";
    private String name;
    private String description;
    private MappingVersion version;
    private List<Transformation> transformations;

}