package io.metadew.iesi.server.rest.script.dto.parameter;

import io.metadew.iesi.metadata.definition.script.ScriptParameter;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptVersionKey;

public interface IScriptParameterDtoService {

    public ScriptParameter convertToEntity(ScriptParameterDto scriptParameterDto, ScriptVersionKey scriptVersionKey);

    public ScriptParameterDto convertToDto(ScriptParameter scriptParameter);

}
