package io.metadew.iesi.metadata.definition;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MetadataObject {

    private String label;
    private String description;
    private String category;

}