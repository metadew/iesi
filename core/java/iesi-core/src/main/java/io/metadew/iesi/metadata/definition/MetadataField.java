package io.metadew.iesi.metadata.definition;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetadataField {

    private String description;
    private int order;
    private MetadataFieldType type;
    private int length;
    private boolean nullable = true;
    private boolean defaultTimestamp = false;
    private boolean primaryKey = false;
    private boolean unique = false;

}