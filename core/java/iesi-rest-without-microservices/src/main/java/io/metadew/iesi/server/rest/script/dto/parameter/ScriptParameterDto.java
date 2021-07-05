package io.metadew.iesi.server.rest.script.dto.parameter;

import io.metadew.iesi.metadata.definition.script.ScriptParameter;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptParameterKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptVersionKey;
import lombok.Data;

@Data
public class ScriptParameterDto {

    private final String name;
    private final String value;

    public ScriptParameter convertToEntity(ScriptVersionKey scriptVersionKey) {
        return new ScriptParameter(new ScriptParameterKey(scriptVersionKey, name), value);
    }

}
