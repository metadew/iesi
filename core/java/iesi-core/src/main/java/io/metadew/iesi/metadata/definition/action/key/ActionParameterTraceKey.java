package io.metadew.iesi.metadata.definition.action.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;

public class ActionParameterTraceKey extends MetadataKey {


    private String runId;
    private Long processId;
    private String actionId;
    private String name;

    public ActionParameterTraceKey(String runId, Long processId, String actionId, String name) {
        this.runId = runId;
        this.processId = processId;
        this.actionId = actionId;
        this.name = name;
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

    public String getName() {
        return name;
    }
}
