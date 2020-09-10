package io.metadew.iesi.metadata.definition.action.design;

import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.metadata.definition.action.design.key.ActionParameterDesignTraceKey;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ActionParameterDesignTrace extends Metadata<ActionParameterDesignTraceKey> {

    private final String value;

    public ActionParameterDesignTrace(ActionParameterDesignTraceKey actionParameterDesignTraceKey, String value) {
        super(actionParameterDesignTraceKey);
        this.value = value;
    }

    public ActionParameterDesignTrace(String runId, Long processId, String actionId, ActionParameter actionParameter) {
        this(new ActionParameterDesignTraceKey(runId, processId, actionId, actionParameter.getMetadataKey().getParameterName()),
                actionParameter.getValue());
    }

    public String getValue() {
        return value;
    }
}
