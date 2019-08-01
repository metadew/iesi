package io.metadew.iesi.metadata.definition.script.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;

public class ScriptVersionDesignTraceKey extends MetadataKey {

    private String runId;
    private Long processId;

    public ScriptVersionDesignTraceKey(String runId, Long processId) {
        this.runId = runId;
        this.processId = processId;
    }

    public String getRunId() {
        return runId;
    }

    public Long getProcessId() {
        return processId;
    }

}
