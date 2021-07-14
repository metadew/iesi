package io.metadew.iesi.server.rest.script.dto.label;

import io.metadew.iesi.metadata.definition.script.ScriptLabel;
import io.metadew.iesi.metadata.definition.script.key.ScriptLabelKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptVersionKey;
import lombok.Data;

import java.util.UUID;

@Data
public class ScriptLabelDto {

    private final String name;
    private final String value;

    public ScriptLabel convertToEntity(ScriptVersionKey scriptVersionKey) {
        return new ScriptLabel(new ScriptLabelKey(UUID.randomUUID().toString()), scriptVersionKey, name, value);
    }

}
