package io.metadew.iesi.metadata.definition.script.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;

public class ScriptTraceKey extends MetadataKey {

    // RUN_ID, PRC_ID, PARENT_PRC_ID, SCRIPT_ID, SCRIPT_TYP_NM, SCRIPT_NM, SCRIPT_DSC

    private String runId;
    private Long processId;

    public ScriptTraceKey(String runId, Long processId) {
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
