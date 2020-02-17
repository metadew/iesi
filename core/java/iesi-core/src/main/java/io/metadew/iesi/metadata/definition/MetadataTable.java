package io.metadew.iesi.metadata.definition;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MetadataTable {

    private String label;
    private String description;
    private String category;
    private Map<String, MetadataField> fields;

}