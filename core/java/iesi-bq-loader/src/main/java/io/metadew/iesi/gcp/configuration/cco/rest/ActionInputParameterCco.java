package io.metadew.iesi.gcp.configuration.cco.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ActionInputParameterCco {

    private String name;
    private String rawValue;
    private String resolvedValue;

}