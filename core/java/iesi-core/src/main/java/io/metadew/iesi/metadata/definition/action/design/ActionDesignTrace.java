package io.metadew.iesi.metadata.definition.action.design;

import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.action.Action;
import io.metadew.iesi.metadata.definition.action.design.key.ActionDesignTraceKey;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ActionDesignTrace extends Metadata<ActionDesignTraceKey> {

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
    public ActionDesignTrace(ActionDesignTraceKey actionDesignTraceKey, Long number, String type, String name, String description, String component, String condition, String iteration, int retries, String errorExpected, String errorStop) {
        super(actionDesignTraceKey);
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

    public ActionDesignTrace(String runId, Long processId, Action action) {
        this(new ActionDesignTraceKey(runId, processId, action.getMetadataKey().getActionId()),
                action.getNumber(), action.getType(), action.getName(), action.getDescription(), action.getComponent(), action.getCondition(), action.getIteration(), action.getRetries(),
                action.getErrorExpected() ? "y" : "n", action.getErrorStop()?"y":"n");
    }

}
