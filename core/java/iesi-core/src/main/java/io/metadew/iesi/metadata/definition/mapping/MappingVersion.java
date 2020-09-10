package io.metadew.iesi.metadata.definition.mapping;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MappingVersion {

    private long number;
    private String description;


}