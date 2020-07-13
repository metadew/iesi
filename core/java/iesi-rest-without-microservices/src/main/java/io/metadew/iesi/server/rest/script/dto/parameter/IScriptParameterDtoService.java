package io.metadew.iesi.server.rest.script.dto.parameter;

import io.metadew.iesi.metadata.definition.script.ScriptParameter;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;

public interface IScriptParameterDtoService {

    public ScriptParameter convertToEntity(ScriptParameterDto scriptParameterDto, ScriptKey scriptKey);

    public ScriptParameterDto convertToDto(ScriptParameter scriptParameter);

}
