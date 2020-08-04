package io.metadew.iesi.server.rest.script.dto;

import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.tools.IdentifierTools;
import io.metadew.iesi.server.rest.script.dto.version.IScriptVersionDtoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ScriptDtoService implements IScriptDtoService {

    private IScriptVersionDtoService scriptVersionDtoService;
    private IScriptDtoRepository scriptDtoRepository;

//    @Autowired
//    public void setScriptVersionDtoService(IScriptVersionDtoService scriptVersionDtoService) {
//        this.scriptVersionDtoService = scriptVersionDtoService;
//    }
//
//    @Autowired
//    public void setScriptDtoRepository(IScriptDtoRepository scriptDtoRepository) {
//        this.scriptDtoRepository = scriptDtoRepository;
//    }

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
    public Page<ScriptDto> getAll(Pageable pageable, List<String> expansions, boolean isLatestOnly) {
        return scriptDtoRepository.getAll(pageable, expansions, isLatestOnly);
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
