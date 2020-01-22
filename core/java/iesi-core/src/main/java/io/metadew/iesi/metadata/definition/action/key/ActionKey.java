package io.metadew.iesi.metadata.definition.action.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
public class ActionKey extends MetadataKey {
    private String actionId;
    private String scriptId;
    private long scriptVersionNumber;

    public ActionKey(String scriptId, long scriptVersionNumber, String actionId) {
        this.actionId = actionId;
        this.scriptId = scriptId;
        this.scriptVersionNumber = scriptVersionNumber;
    }

    public String getActionId() {
        return actionId;
    }

    public String getScriptId() {
        return scriptId;
    }

    public long getScriptVersionNumber() {
        return scriptVersionNumber;
    }
}
