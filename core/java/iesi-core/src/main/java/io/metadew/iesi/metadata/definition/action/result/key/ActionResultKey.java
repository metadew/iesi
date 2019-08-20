package io.metadew.iesi.metadata.definition.action.result.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;

public class ActionResultKey extends MetadataKey {

    private String runId;
    private Long processId;
    private String actionId;

    public ActionResultKey(String runId, Long processId, String actionId) {
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
