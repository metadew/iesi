package io.metadew.iesi.gcp.configuration.cco.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetadataFieldCco {

    private String description;
    private int order;
    private String type;
    private int length;
    private boolean nullable = true;
    private boolean defaultTimestamp = false;
    private boolean primaryKey = false;
    private boolean unique = false;

}