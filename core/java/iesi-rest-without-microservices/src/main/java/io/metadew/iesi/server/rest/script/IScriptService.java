package io.metadew.iesi.server.rest.script;

import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.server.rest.script.dto.ScriptDto;
import io.metadew.iesi.server.rest.script.dto.ScriptPostDto;

import java.util.List;
import java.util.Optional;

public interface IScriptService {

    public List<Script> getAll();

    public List<Script> getByName(String name);

    public Optional<Script> getByNameAndVersion(String name, long version);

    public void createScript(ScriptPostDto scriptDto);

    public void updateScript(ScriptPostDto scriptPostDto);

    public void updateScripts(List<ScriptPostDto> scriptPostDtos);

    public void deleteAll();

    public void deleteByName(String name);

    public void deleteByNameAndVersion(String name, long version);

}
