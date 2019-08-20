package io.metadew.iesi.metadata.definition.script.result.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;

public class ScriptResultOutputKey extends MetadataKey {

    // RUN_ID, PRC_ID, PARENT_PRC_ID, SCRIPT_ID, SCRIPT_TYP_NM, SCRIPT_NM, SCRIPT_DSC

    private String runId;
    private Long processId;
    private String outputName;

    public ScriptResultOutputKey(String runId, Long processId, String outputName) {
        this.runId = runId;
        this.processId = processId;
        this.outputName = outputName;
    }

    public String getRunId() {
        return runId;
    }

    public Long getProcessId() {
        return processId;
    }

    public String getOutputName() {
        return outputName;
    }
}
