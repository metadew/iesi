package io.metadew.iesi.metadata.definition.action;

import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.action.key.ActionParameterTraceKey;

public class ActionParameterTrace extends Metadata<ActionParameterTraceKey> {

    private final String value;

    public ActionParameterTrace(ActionParameterTraceKey metadataKey, String value) {
        super(metadataKey);
        this.value = value;
    }

    public ActionParameterTrace(String runId, Long processId, String actionId, ActionParameter actionParameter) {
        this(new ActionParameterTraceKey(runId, processId, actionId, actionParameter.getName()), actionParameter.getValue());
    }

    public String getValue() {
        return value;
    }
}
