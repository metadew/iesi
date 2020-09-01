package io.metadew.iesi.server.rest.script.dto;

import io.metadew.iesi.server.rest.script.ScriptFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IScriptDtoRepository {

    Page<ScriptDto> getAll(Pageable pageable, List<String> expansions, boolean isLatestVersionOnly, List<ScriptFilter> scriptFilters);

    Page<ScriptDto> getByName(Pageable pageable, String name, List<String> expansions, boolean isLatestOnly);

    Optional<ScriptDto> getByNameAndVersion(String name, long version, List<String> expansions);

}
