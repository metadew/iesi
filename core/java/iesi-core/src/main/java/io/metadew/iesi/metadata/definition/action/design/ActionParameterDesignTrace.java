package io.metadew.iesi.metadata.definition.action.design;

import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.metadata.definition.action.design.key.ActionParameterDesignTraceKey;

public class ActionParameterDesignTrace extends Metadata<ActionParameterDesignTraceKey> {

    private final String value;

    public ActionParameterDesignTrace(ActionParameterDesignTraceKey actionParameterDesignTraceKey, String value) {
        super(actionParameterDesignTraceKey);
        this.value = value;
    }

    public ActionParameterDesignTrace(String runId, Long processId, String actionId, ActionParameter actionParameter) {
        this(new ActionParameterDesignTraceKey(runId, processId, actionId, actionParameter.getName()),
                actionParameter.getValue());
    }

    public String getValue() {
        return value;
    }
}
