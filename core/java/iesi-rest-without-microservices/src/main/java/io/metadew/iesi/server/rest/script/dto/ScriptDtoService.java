package io.metadew.iesi.server.rest.script.dto;

import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.tools.IdentifierTools;
import io.metadew.iesi.server.rest.script.dto.action.IScriptActionDtoService;
import io.metadew.iesi.server.rest.script.dto.label.IScriptLabelDtoService;
import io.metadew.iesi.server.rest.script.dto.parameter.IScriptParameterDtoService;
import io.metadew.iesi.server.rest.script.dto.version.IScriptVersionDtoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ScriptDtoService implements IScriptDtoService {

    private IScriptParameterDtoService scriptParameterDtoService;
    private IScriptLabelDtoService scriptLabelDtoService;
    private IScriptActionDtoService scriptActionDtoService;
    private IScriptVersionDtoService scriptVersionDtoService;
    private IScriptDtoRepository scriptDtoRepository;

    @Autowired
    public ScriptDtoService(IScriptParameterDtoService scriptParameterDtoService, IScriptLabelDtoService scriptLabelDtoService,
                            IScriptActionDtoService scriptActionDtoService, IScriptVersionDtoService scriptVersionDtoService,
                            IScriptDtoRepository scriptDtoRepository) {
        this.scriptActionDtoService = scriptActionDtoService;
        this.scriptLabelDtoService = scriptLabelDtoService;
        this.scriptParameterDtoService = scriptParameterDtoService;
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

    public ScriptDto convertToDto(Script script) {
        return new ScriptDto(script.getName(), script.getDescription(),
                scriptVersionDtoService.convertToDto(script.getVersion()),
                script.getParameters().stream().map(scriptParameterDtoService::convertToDto).collect(Collectors.toList()),
                script.getActions().stream().map(scriptActionDtoService::convertToDto).collect(Collectors.toList()),
                script.getLabels().stream().map(scriptLabelDtoService::convertToDto).collect(Collectors.toList()),
                null,
                null);
    }

    @Override
    public List<ScriptDto> getAll() {
        return getAll(new ArrayList<>());
    }

    @Override
    public List<ScriptDto> getAll(List<String> expansions) {
        return getAll(expansions, false);
    }

    @Override
    public List<ScriptDto> getAll(List<String> expansions, boolean isLatestOnly) {
        return scriptDtoRepository.getAll(expansions, isLatestOnly);
    }

    @Override
    public List<ScriptDto> getByName(String name) {
        return getByName(name, new ArrayList<>());
    }

    @Override
    public List<ScriptDto> getByName(String name, List<String> expansions) {
        return scriptDtoRepository.getByName(name, expansions);
    }
    @Override
    public Optional<ScriptDto> getByNameAndVersion(String name, long version) {
        return getByNameAndVersion(name, version, new ArrayList<>());
    }

    @Override
    public Optional<ScriptDto> getByNameAndVersion(String name, long version, List<String> expansions) {
        return scriptDtoRepository.getByNameAndVersion(name, version, expansions);
    }
}
