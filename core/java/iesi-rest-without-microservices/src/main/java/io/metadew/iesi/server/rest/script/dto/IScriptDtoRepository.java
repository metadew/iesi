package io.metadew.iesi.server.rest.script.dto;

import java.util.List;
import java.util.Optional;

public interface IScriptDtoRepository {

    List<ScriptDto> getAll(int limit, int pageNumber,List<String> expansions, boolean isLatestOnly);

    int getTotalPages(int limit, List<String> expansions, boolean isLatestVersionOnly);

    List<ScriptDto> getByName(String name, List<String> expansions);

    Optional<ScriptDto> getByNameAndVersion(String name, long version, List<String> expansions);

}
