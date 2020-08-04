package io.metadew.iesi.server.rest.script.dto;

import io.metadew.iesi.metadata.definition.script.Script;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IScriptDtoService {

    Script convertToEntity(ScriptDto scriptDto);

    Page<ScriptDto> getAll(Pageable pageable, List<String> expansions, boolean isLatestOnly);

    List<ScriptDto> getByName(String name);

    List<ScriptDto> getByName(String name, List<String> expansions);

    Optional<ScriptDto> getByNameAndVersion(String name, long version);

    Optional<ScriptDto> getByNameAndVersion(String name, long version, List<String> expansions);
}
