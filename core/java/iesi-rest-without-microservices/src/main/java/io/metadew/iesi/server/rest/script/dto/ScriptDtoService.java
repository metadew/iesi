package io.metadew.iesi.server.rest.script.dto;

import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.tools.IdentifierTools;
import io.metadew.iesi.server.rest.script.ScriptFilter;
import io.metadew.iesi.server.rest.script.dto.version.IScriptVersionDtoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ScriptDtoService implements IScriptDtoService {

    private final IScriptVersionDtoService scriptVersionDtoService;
    private final IScriptDtoRepository scriptDtoRepository;

    @Autowired
    public ScriptDtoService(IScriptVersionDtoService scriptVersionDtoService,
                            IScriptDtoRepository scriptDtoRepository) {
        this.scriptVersionDtoService = scriptVersionDtoService;
        this.scriptDtoRepository = scriptDtoRepository;
    }

    public Script convertToEntity(ScriptDto scriptDto) {
        return new Script(
                new ScriptKey(IdentifierTools.getScriptIdentifier(scriptDto.getName()),
                        scriptVersionDtoService.convertToEntity(scriptDto.getVersion(), IdentifierTools.getScriptIdentifier(scriptDto.getName())).getNumber()),
                scriptDto.getName(),
                scriptDto.getDescription(),
                scriptVersionDtoService.convertToEntity(scriptDto.getVersion(), IdentifierTools.getScriptIdentifier(scriptDto.getName())),
                scriptDto.getParameters().stream()
                        .map(parameter -> parameter.convertToEntity(new ScriptKey(IdentifierTools.getScriptIdentifier(scriptDto.getName()), scriptDto.getVersion().getNumber())))
                        .collect(Collectors.toList()),
                scriptDto.getActions().stream()
                        .map(action -> action.convertToEntity(IdentifierTools.getScriptIdentifier(scriptDto.getName()), scriptDto.getVersion().getNumber()))
                        .collect(Collectors.toList()),
                scriptDto.getLabels().stream()
                        .map(label -> label.convertToEntity(new ScriptKey(IdentifierTools.getScriptIdentifier(scriptDto.getName()), scriptDto.getVersion().getNumber())))
                        .collect(Collectors.toList()));
    }

    @Override
    public Page<ScriptDto> getAll(Pageable pageable, List<String> expansions, boolean isLatestVersionOnly, List<ScriptFilter> scriptFilters) {
        return scriptDtoRepository.getAll(pageable, expansions, isLatestVersionOnly, scriptFilters);
    }

    @Override
    public Page<ScriptDto> getByName(Pageable pageable, String name, List<String> expansions, boolean isLatestOnly) {
        return scriptDtoRepository.getByName(pageable, name, expansions, isLatestOnly);
    }

    @Override
    public Optional<ScriptDto> getByNameAndVersion(String name, long version, List<String> expansions) {
        return scriptDtoRepository.getByNameAndVersion(name, version, expansions);
    }
}
