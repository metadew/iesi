package io.metadew.iesi.gcp.configuration.cco.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode
public class ExecutionInputParameterCco {

    private final String name;
    private final String value;

}