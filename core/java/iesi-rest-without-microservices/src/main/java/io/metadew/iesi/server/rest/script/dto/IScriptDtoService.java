package io.metadew.iesi.server.rest.script.dto;

import io.metadew.iesi.metadata.definition.script.Script;

import java.util.List;
import java.util.Optional;

public interface IScriptDtoService {

    Script convertToEntity(ScriptDto scriptDto);

    ScriptDto convertToDto(Script script);

    int getTotalPages(int limit, List<String> expansions, boolean isLatestVersionOnly);

    List<ScriptDto> getAll(int limit, int pageNumber, List<String> expansions, boolean isLatestOnly);

    List<ScriptDto> getByName(String name);

    List<ScriptDto> getByName(String name, List<String> expansions);

    Optional<ScriptDto> getByNameAndVersion(String name, long version);

    Optional<ScriptDto> getByNameAndVersion(String name, long version, List<String> expansions);
}
