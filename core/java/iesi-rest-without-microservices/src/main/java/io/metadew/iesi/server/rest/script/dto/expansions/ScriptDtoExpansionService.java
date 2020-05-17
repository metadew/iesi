package io.metadew.iesi.server.rest.script.dto.expansions;

import io.metadew.iesi.server.rest.script.dto.ScriptDto;

import java.util.List;

public class ScriptDtoExpansionService implements IScriptDtoExpansionService {

    public void addExpansions(ScriptDto scriptDto, List<String> expansions) {
        if (expansions == null) return;
        if (expansions.contains("execution")) {
            // TODO
        }
        if (expansions.contains("scheduling")) {
            // TODO
        }
    }

}
