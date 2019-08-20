package io.metadew.iesi.metadata.definition.action.result.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;

public class ActionResultOutputKey extends MetadataKey {

    private String runId;
    private Long processId;
    private String actionId;
    private String outputName;

    public ActionResultOutputKey(String runId, Long processId, String actionId, String outputName) {
        this.runId = runId;
        this.processId = processId;
        this.actionId = actionId;
        this.outputName = outputName;
    }

    public String getRunId() {
        return runId;
    }

    public Long getProcessId() {
        return processId;
    }

    public String getActionId() {
        return actionId;
    }

    public String getOutputName() {
        return outputName;
    }
}
