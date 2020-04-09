package io.metadew.iesi.metadata.definition.action.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ActionParameterKey extends MetadataKey {
    private final ActionKey actionKey;
    private final String parameterName;

    public ActionParameterKey(String scriptId, long scriptVersionNumber, String actionId, String parameterName) {
        this.actionKey = new ActionKey(new ScriptKey(scriptId, scriptVersionNumber), actionId);
        this.parameterName = parameterName;
    }

}
