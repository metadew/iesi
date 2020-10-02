package io.metadew.iesi.gcp.configuration.cco.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MetadataTableCco {

    private String name;
    private String label;
    private String description;
    private String category;
    private Map<String, MetadataFieldCco> fields;

}