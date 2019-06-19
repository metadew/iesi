package io.metadew.iesi.server.rest.resource.script.dto;

import io.metadew.iesi.metadata.definition.Script;
import io.metadew.iesi.metadata.definition.ScriptParameter;
import org.springframework.hateoas.ResourceSupport;

import java.util.List;
import java.util.Optional;

public class ScriptExecutionDto  {

    private String script;
    private Long version;
    private String environment;
    private List<ScriptParameter> parameters;


    public ScriptExecutionDto(){}

    public ScriptExecutionDto(String script, Long version, String environment, List<ScriptParameter> parameters) {
        this.script = script;
        this.version = version;
        this.environment = environment;
        this.parameters = parameters;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public List<ScriptParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<ScriptParameter> parameters) {
        this.parameters = parameters;
    }
}
