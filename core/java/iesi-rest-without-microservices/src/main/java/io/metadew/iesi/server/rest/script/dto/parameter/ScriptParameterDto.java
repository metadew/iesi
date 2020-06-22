package io.metadew.iesi.server.rest.script.dto.parameter;

import io.metadew.iesi.metadata.definition.script.ScriptParameter;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptParameterKey;
import lombok.Data;

@Data
public class ScriptParameterDto {

    private final String name;
    private final String value;

    public ScriptParameter convertToEntity(ScriptKey scriptKey) {
        return new ScriptParameter(new ScriptParameterKey(scriptKey, name), value);
    }

}
