package io.metadew.iesi.metadata.definition.action.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ActionKey extends MetadataKey {

    private final ScriptKey scriptKey;
    private final String actionId;

    public ActionKey(String scriptId, long scriptVersionNumber, String actionId) {
        this.scriptKey = new ScriptKey(scriptId, scriptVersionNumber);
        this.actionId = actionId;
    }

}
