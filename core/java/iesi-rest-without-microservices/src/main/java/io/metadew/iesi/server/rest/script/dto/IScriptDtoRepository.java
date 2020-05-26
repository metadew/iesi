package io.metadew.iesi.server.rest.script.dto;

import java.util.List;
import java.util.Optional;

public interface IScriptDtoRepository {

    public List<ScriptDto> getAll(List<String> expansions);

    public List<ScriptDto> getByName(String name, List<String> expansions);

    public Optional<ScriptDto> getByNameAndVersion(String name, long version, List<String> expansions);

}
