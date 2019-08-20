package io.metadew.iesi.metadata.definition.script.trace.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;

public class ScriptParameterTraceKey extends MetadataKey {

    private String runId;
    private Long processId;
    private String scriptParameterName;

    public ScriptParameterTraceKey(String runId, Long processId, String scriptParameterName) {
        this.runId = runId;
        this.processId = processId;
        this.scriptParameterName = scriptParameterName;
    }

    public String getRunId() {
        return runId;
    }

    public Long getProcessId() {
        return processId;
    }


    public String getScriptParameterName() {
        return scriptParameterName;
    }

}
