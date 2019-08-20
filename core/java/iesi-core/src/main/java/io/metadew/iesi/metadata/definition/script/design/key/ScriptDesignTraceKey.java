package io.metadew.iesi.metadata.definition.script.design.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;

public class ScriptDesignTraceKey extends MetadataKey {

    private String runId;
    private Long processId;

    public ScriptDesignTraceKey(String runId, Long processId) {
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
