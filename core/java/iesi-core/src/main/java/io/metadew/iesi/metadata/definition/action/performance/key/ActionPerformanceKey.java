package io.metadew.iesi.metadata.definition.action.performance.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;

public class ActionPerformanceKey extends MetadataKey {

    private String runId;
    private Long procedureId;
    private String scope;

    public ActionPerformanceKey(String runId, Long procedureId, String scope) {
        this.runId = runId;
        this.procedureId = procedureId;
        this.scope = scope;
    }

    public String getRunId() {
        return runId;
    }

    public Long getProcedureId() {
        return procedureId;
    }

    public String getScope() {
        return scope;
    }
}
