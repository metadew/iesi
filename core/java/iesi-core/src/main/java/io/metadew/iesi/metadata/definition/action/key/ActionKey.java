package io.metadew.iesi.metadata.definition.action.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class ActionKey extends MetadataKey {

    private final ScriptKey scriptKey;
    private final String actionId;

}
