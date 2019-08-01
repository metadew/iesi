package io.metadew.iesi.metadata.definition.action.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;

public class ActionTraceKey extends MetadataKey {

    private String runId;
    private Long processId;
    private String actionId;

    public ActionTraceKey(String runId, Long processId, String actionId) {
        this.runId = runId;
        this.processId = processId;
        this.actionId = actionId;
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

}
