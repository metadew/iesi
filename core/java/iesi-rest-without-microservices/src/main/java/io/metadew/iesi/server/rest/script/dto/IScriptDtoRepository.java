package io.metadew.iesi.server.rest.script.dto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IScriptDtoRepository {

    Page<ScriptDto> getAll(Pageable pageable, List<String> expansions, boolean isLatestOnly);

    List<ScriptDto> getByName(String name, List<String> expansions);

    Optional<ScriptDto> getByNameAndVersion(String name, long version, List<String> expansions);

}
