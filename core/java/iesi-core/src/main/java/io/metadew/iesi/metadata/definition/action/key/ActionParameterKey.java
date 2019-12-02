package io.metadew.iesi.metadata.definition.action.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;

public class ActionParameterKey extends MetadataKey {
    private String actionId;
    private String actionName;
    private String scriptId;
    private long scriptVersionNumber;

    public ActionParameterKey(String scriptId, long scriptVersionNumber, String actionId, String actionName) {
        this.actionId = actionId;
        this.actionName = actionName;
        this.scriptId = scriptId;
        this.scriptVersionNumber = scriptVersionNumber;
    }

    public String getActionId() {
        return actionId;
    }

    public String getActionName() {
        return actionName;
    }

    public String getScriptId() {
        return scriptId;
    }

    public long getScriptVersionNumber() {
        return scriptVersionNumber;
    }
}
