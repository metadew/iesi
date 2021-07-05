package io.metadew.iesi.server.rest.script.dto;

import io.metadew.iesi.metadata.definition.script.ScriptVersion;

public interface IScriptPostDtoService {

    public ScriptVersion convertToEntity(ScriptPostDto scriptPostDto);


}
