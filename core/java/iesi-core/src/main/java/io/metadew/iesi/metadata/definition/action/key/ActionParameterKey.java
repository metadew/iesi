package io.metadew.iesi.metadata.definition.action.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;

public class ActionParameterKey extends MetadataKey {
    private final ActionKey actionKey;
    private String parameterName;

    public ActionParameterKey(String scriptId, long scriptVersionNumber, String actionId, String parameterName) {
        this.actionKey = new ActionKey(scriptId, scriptVersionNumber, actionId);
        this.parameterName = parameterName;
    }

    public ActionParameterKey(ActionKey actionKey, String parameterName) {
        this.actionKey = actionKey;
        this.parameterName = parameterName;
    }

    public String getParameterName() {
        return parameterName;
    }

    public ActionKey getActionKey() {
        return actionKey;
    }
}
