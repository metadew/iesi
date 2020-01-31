package io.metadew.iesi.metadata.definition.action.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
public class ActionKey extends MetadataKey {

    private final ScriptKey scriptKey;
    private String actionId;

    public ActionKey(String scriptId, long scriptVersionNumber, String actionId) {
        this.scriptKey = new ScriptKey(scriptId, scriptVersionNumber);
        this.actionId = actionId;
    }

    public ActionKey(ScriptKey scriptKey, String actionId) {
        this.scriptKey = scriptKey;
        this.actionId = actionId;
    }

    public String getActionId() {
        return actionId;
    }

    public ScriptKey getScriptKey() {
        return scriptKey;
    }
}
