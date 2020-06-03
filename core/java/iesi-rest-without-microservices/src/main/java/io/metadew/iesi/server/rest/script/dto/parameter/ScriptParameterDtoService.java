package io.metadew.iesi.server.rest.script.dto.parameter;

import io.metadew.iesi.metadata.definition.script.ScriptParameter;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptParameterKey;
import org.springframework.stereotype.Service;

@Service
public class ScriptParameterDtoService implements IScriptParameterDtoService {

    public ScriptParameter convertToEntity(ScriptParameterDto scriptParameterDto, ScriptKey scriptKey) {
        return new ScriptParameter(new ScriptParameterKey(scriptKey, scriptParameterDto.getName()), scriptParameterDto.getValue());
    }

    public ScriptParameterDto convertToDto(ScriptParameter scriptParameter) {
        return new ScriptParameterDto(scriptParameter.getMetadataKey().getParameterName(), scriptParameter.getValue());
    }

}
