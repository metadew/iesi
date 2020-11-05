package io.metadew.iesi.metadata.definition.action.trace;

import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.action.Action;
import io.metadew.iesi.metadata.definition.action.trace.key.ActionTraceKey;
import lombok.Builder;

public class ActionTrace extends Metadata<ActionTraceKey> {


    private long number;
    private String type;
    private String name;
    private String description;
    private String component;
    private String condition;
    private String iteration;
    private String errorExpected;
    private String errorStop;
    private int retries;

    @Builder
    public ActionTrace(ActionTraceKey actionTraceKey, Long number, String type, String name, String description, String component, String condition, String iteration, int retries, String errorExpected, String errorStop) {
        super(actionTraceKey);
        this.number = number;
        this.type = type;
        this.name = name;
        this.description = description;
        this.component = component;
        this.condition = condition;
        this.iteration = iteration;
        this.errorExpected = errorExpected;
        this.errorStop = errorStop;
        this.retries = retries;
    }

    public ActionTrace(String runId, Long processId, Action action) {
        this(new ActionTraceKey(runId, processId, action.getMetadataKey().getActionId()), action.getNumber(), action.getType(), action.getName(),
                action.getDescription(), action.getComponent(), action.getCondition(), action.getIteration(), action.getRetries(),
                action.getErrorExpected()?"y":"n", action.getErrorStop()?"y":"n");
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getComponent() {
        return component;
    }

    public String getCondition() {
        return condition;
    }

    public String getIteration() {
        return iteration;
    }

    public String getErrorExpected() {
        return errorExpected;
    }

    public String getErrorStop() {
        return errorStop;
    }

    public int getRetries() {
        return retries;
    }

    public long getNumber() {
        return number;
    }
}
