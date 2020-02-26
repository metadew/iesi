package io.metadew.iesi.server.rest.resource.script.dto;

import io.metadew.iesi.metadata.definition.script.ScriptParameter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class ScriptExecutionDto  {

    private String script;
    private Long version;
    private String environment;
    private List<ScriptParameter> parameters;
}
