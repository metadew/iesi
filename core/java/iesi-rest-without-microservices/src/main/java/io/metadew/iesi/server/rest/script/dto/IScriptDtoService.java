package io.metadew.iesi.server.rest.script.dto;

import io.metadew.iesi.metadata.definition.script.Script;

import java.util.List;
import java.util.Optional;

public interface IScriptDtoService {

    public Script convertToEntity(ScriptDto scriptDto);

    public ScriptDto convertToDto(Script script);

    public List<ScriptDto> getAll();

    public List<ScriptDto> getByName(String name);

    public List<ScriptDto> getByNameAndVersion(String name, long version);

}
