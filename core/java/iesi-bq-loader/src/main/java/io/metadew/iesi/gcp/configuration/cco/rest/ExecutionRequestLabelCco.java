package io.metadew.iesi.gcp.configuration.cco.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExecutionRequestLabelCco {

    private String name;
    private String value;

}