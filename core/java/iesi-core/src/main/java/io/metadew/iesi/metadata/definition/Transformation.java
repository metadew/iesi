package io.metadew.iesi.metadata.definition;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transformation {

    private long number;
    private String type = "default";
    private String leftField;
    private String rightField;

}