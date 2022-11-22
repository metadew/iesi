package io.metadew.iesi.server.rest.script.dto;

import io.metadew.iesi.metadata.definition.script.Script;

import java.util.List;

public interface IScriptPostDtoService {

    Script convertToEntity(ScriptPostDto scriptPostDto);

    List<Script> convertToEntities(List<ScriptPostDto> scriptPostDtos);

    ScriptPostDto convertToDto(Script script);

}
