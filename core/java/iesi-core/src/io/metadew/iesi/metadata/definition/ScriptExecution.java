package io.metadew.iesi.metadata.definition;

import java.util.List;

public class ScriptExecution {

    private String script;
    private String version;
    private String environment;
    private List<ScriptExecutionParameter> parameters;

    // Constructors
    public ScriptExecution() {

    }

    // Getters and Setters
    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<ScriptExecutionParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<ScriptExecutionParameter> parameters) {
        this.parameters = parameters;
    }


}