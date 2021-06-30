package io.metadew.iesi.server.rest.script.dto.label;

import io.metadew.iesi.metadata.definition.script.ScriptLabel;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptLabelKey;
import lombok.Data;

import java.util.UUID;

@Data
public class ScriptLabelDto {

    private final String name;
    private final String value;

    public ScriptLabel convertToEntity(ScriptKey scriptKey) {
        return new ScriptLabel(new ScriptLabelKey(UUID.randomUUID().toString()), scriptKey, name, value);
    }

}
