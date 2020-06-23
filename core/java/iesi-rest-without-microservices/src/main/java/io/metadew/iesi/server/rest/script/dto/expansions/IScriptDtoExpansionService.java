package io.metadew.iesi.server.rest.script.dto.expansions;

import io.metadew.iesi.server.rest.script.dto.ScriptDto;

import java.util.List;

public interface IScriptDtoExpansionService {
    public void addExpansions(ScriptDto scriptDto, List<String> expansions);
}
