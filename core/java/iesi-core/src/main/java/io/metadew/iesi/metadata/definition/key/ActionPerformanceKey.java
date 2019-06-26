package io.metadew.iesi.metadata.definition.key;

public class ActionPerformanceKey extends MetadataKey {

    private String runId;
    private Long procedureId;
    private String actionId;
    private String scope;

    public ActionPerformanceKey(String runId, Long procedureId, String actionId, String scope) {
        this.runId = runId;
        this.procedureId = procedureId;
        this.actionId = actionId;
        this.scope = scope;
    }

    public String getRunId() {
        return runId;
    }

    public Long getProcedureId() {
        return procedureId;
    }

    public String getActionId() {
        return actionId;
    }

    public String getScope() {
        return scope;
    }
}
