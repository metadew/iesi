package io.metadew.iesi.server.rest.script.dto;

import io.metadew.iesi.metadata.definition.script.Script;

public interface IScriptPostDtoService {

    public Script convertToEntity(ScriptPostDto scriptPostDto);

    public ScriptPostDto convertToDto(Script script);

}
