package io.metadew.iesi.server.rest.script.dto;

import io.metadew.iesi.server.rest.script.ScriptFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

public interface IScriptDtoRepository {

    Page<ScriptDto> getAll(Authentication authentication, Pageable pageable, List<String> expansions, boolean isLatestVersionOnly, List<ScriptFilter> scriptFilters);

    Page<ScriptDto> getAllActive(Authentication authentication, Pageable pageable, List<String> expansions, boolean isLatestVersionOnly, List<ScriptFilter> scriptFilters);

    Page<ScriptDto> getAllInActive(Authentication authentication, Pageable pageable, List<String> expansions, boolean isLatestVersionOnly, List<ScriptFilter> scriptFilters);

    Page<ScriptDto> getAllByName(Authentication authentication, Pageable pageable, String name, List<String> expansions, boolean isLatestOnly);

    Page<ScriptDto> getByName(Authentication authentication, Pageable pageable, String name, List<String> expansions, boolean isLatestOnly);

    Optional<ScriptDto> getByNameAndVersion(Authentication authentication, String name, long version, List<String> expansions);

    Page<ScriptDto> getAllByNameAndVersion(Authentication authentication, Pageable pageable, String name, long version, List<String> expansions);

}
