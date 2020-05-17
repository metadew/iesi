package io.metadew.iesi.server.rest.script.dto;

import io.metadew.iesi.metadata.definition.script.Script;

public interface IScriptDtoService {

    public Script convertToEntity(ScriptDto scriptDto);

    public ScriptDto convertToDto(Script script);

}
