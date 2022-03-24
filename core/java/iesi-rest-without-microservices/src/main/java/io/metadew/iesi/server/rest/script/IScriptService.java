package io.metadew.iesi.server.rest.script;

import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.server.rest.script.dto.ScriptPostDto;

import java.util.List;
import java.util.Optional;

public interface IScriptService {

    List<Script> getAll();

    List<Script> getByName(String name);

    Optional<Script> getByNameAndVersion(String name, long version);

    void createScript(Script script);
    List<Script> importScripts(String textPlain);

    void updateScript(Script script);

    void updateScripts(List<Script> scripts);

    void deleteAll();

    void deleteByName(String name);

    void deleteByNameAndVersion(String name, long version);
}
