package io.metadew.iesi.server.rest.script.dto.expansions;

import io.metadew.iesi.metadata.configuration.script.result.ScriptResultConfiguration;
import io.metadew.iesi.server.rest.script.dto.ScriptDto;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ScriptDtoExpansionService implements IScriptDtoExpansionService {

    private ScriptResultConfiguration scriptResultConfiguration;

    @Autowired
    public ScriptDtoExpansionService(ScriptResultConfiguration scriptResultConfiguration) {
        this.scriptResultConfiguration = scriptResultConfiguration;
    }

    public void addExpansions(ScriptDto scriptDto, List<String> expansions) {
        if (expansions == null) return;
        if (expansions.contains("execution")) {
//            scriptDto.setScriptExecutionInformation(new ScriptExecutionInformation(
//                    scriptResultConfiguration.getCount("tst", scriptDto.getName(), scriptDto.getVersion().getNumber()),
//                    scriptResultConfiguration.getLatestScriptResult("tst", scriptDto.getName(), scriptDto.getVersion().getNumber())
//                    .map(scriptResult -> new ScriptExecutionDto(scriptResult.getMetadataKey().getRunId()))
//                    .orElse(null)
//                    ));
        }
        if (expansions.contains("scheduling")) {
            // TODO
        }
    }

}
