package io.metadew.iesi.server.rest.script.dto;

import java.util.List;
import java.util.Optional;

public interface IScriptDtoRepository {

    List<ScriptDto> getAll(List<String> expansions, boolean isLatestOnly,int limit, int pageNumber, List<String> column, List<String> sort);

    List<ScriptDto> getByName(String name, List<String> expansions);

    Optional<ScriptDto> getByNameAndVersion(String name, long version, List<String> expansions);

}
