package io.metadew.iesi.server.rest.resource.script.dto;

import org.springframework.hateoas.ResourceSupport;

public class ScriptExecutionFinalDto extends ResourceSupport {

    private String run_id;
    private String script;
    private String version;
    private String environment;
    private String start_timestamp;
    private String end_timestamp;

    public ScriptExecutionFinalDto(){}


    public ScriptExecutionFinalDto(String run_id, String script, String version, String environment, String start_timestamp, String end_timestamp) {
        this.run_id = run_id;
        this.script = script;
        this.version = version;
        this.environment = environment;
        this.start_timestamp = start_timestamp;
        this.end_timestamp = end_timestamp;
    }

    public String getRun_id() {
        return run_id;
    }

    public void setRun_id(String run_id) {
        this.run_id = run_id;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getStart_timestamp() {
        return start_timestamp;
    }

    public void setStart_timestamp(String start_timestamp) {
        this.start_timestamp = start_timestamp;
    }

    public String getEnd_timestamp() {
        return end_timestamp;
    }

    public void setEnd_timestamp(String end_timestamp) {
        this.end_timestamp = end_timestamp;
    }
}
