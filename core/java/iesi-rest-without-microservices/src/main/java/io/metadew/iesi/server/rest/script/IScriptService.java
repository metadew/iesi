package io.metadew.iesi.server.rest.script;

import io.metadew.iesi.metadata.definition.script.ScriptVersion;
import io.metadew.iesi.server.rest.script.dto.ScriptPostDto;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface IScriptService {

    Set<ScriptVersion> getAll();

    Set<ScriptVersion> getByName(String name);

    Optional<ScriptVersion> getByNameAndVersion(String name, long version);

    void createScript(ScriptPostDto scriptDto);

    void updateScript(ScriptPostDto scriptPostDto);

    void updateScripts(List<ScriptPostDto> scriptPostDtos);

    void deleteAll();

    void deleteByName(String name);

    void deleteByNameAndVersion(String name, long version);

    boolean existsByNameAndVersion(String name, long version);

    boolean existsByName(String name);

    boolean existsDeleted(String name);

}
