package io.metadew.iesi.server.rest.script.dto;

import io.metadew.iesi.metadata.configuration.security.SecurityGroupConfiguration;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.definition.security.SecurityGroup;
import io.metadew.iesi.metadata.tools.IdentifierTools;
import io.metadew.iesi.server.rest.script.ScriptFilter;
import io.metadew.iesi.server.rest.script.dto.version.IScriptVersionDtoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ScriptDtoService implements IScriptDtoService {

    private final IScriptVersionDtoService scriptVersionDtoService;
    private final IScriptDtoRepository scriptDtoRepository;
    private final SecurityGroupConfiguration securityGroupConfiguration;

    @Autowired
    public ScriptDtoService(IScriptVersionDtoService scriptVersionDtoService, IScriptDtoRepository scriptDtoRepository,
                            SecurityGroupConfiguration securityGroupConfiguration) {
        this.scriptVersionDtoService = scriptVersionDtoService;
        this.scriptDtoRepository = scriptDtoRepository;
        this.securityGroupConfiguration = securityGroupConfiguration;
    }

    public Script convertToEntity(ScriptDto scriptDto) {
        SecurityGroup securityGroup = securityGroupConfiguration.getByName(scriptDto.getSecurityGroupName())
                .orElseThrow(() -> new RuntimeException("could not find Security Group with name " + scriptDto.getSecurityGroupName()));
        return new Script(
                new ScriptKey(IdentifierTools.getScriptIdentifier(scriptDto.getName()),
                        scriptVersionDtoService.convertToEntity(scriptDto.getVersion(), IdentifierTools.getScriptIdentifier(scriptDto.getName())).getNumber()),
                securityGroup.getMetadataKey(),
                scriptDto.getSecurityGroupName(),
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
                        .collect(Collectors.toList()),
                scriptDto.getDeletedAt());
    }

    @Override
    public Page<ScriptDto> getAll(Authentication authentication, Pageable pageable, List<String> expansions, boolean isLatestVersionOnly, List<ScriptFilter> scriptFilters) {
        return scriptDtoRepository.getAll(authentication, pageable, expansions, isLatestVersionOnly, scriptFilters);
    }

    @Override
    public Page<ScriptDto> getAllActive(Authentication authentication, Pageable pageable, List<String> expansions, boolean isLatestVersionOnly, List<ScriptFilter> scriptFilters) {
        return  scriptDtoRepository.getAllActive(authentication, pageable, expansions, isLatestVersionOnly, scriptFilters);
    }

    @Override
    public Page<ScriptDto> getAllInActive(Authentication authentication, Pageable pageable, List<String> expansions, boolean isLatestVersionOnly, List<ScriptFilter> scriptFilters) {
        return scriptDtoRepository.getAllInActive(authentication, pageable, expansions, isLatestVersionOnly, scriptFilters);
    }

    @Override
    public Page<ScriptDto> getAllByName(Authentication authentication, Pageable pageable, String name, List<String> expansions, boolean isLatestOnly) {
        return scriptDtoRepository.getAllByName(authentication, pageable, name, expansions, isLatestOnly);
    }

    @Override
    public Page<ScriptDto> getByName(Authentication authentication, Pageable pageable, String name, List<String> expansions, boolean isLatestOnly) {
        return scriptDtoRepository.getByName(authentication, pageable, name, expansions, isLatestOnly);
    }

    @Override
    public Optional<ScriptDto> getByNameAndVersion(Authentication authentication, String name, long version, List<String> expansions) {
        return scriptDtoRepository.getByNameAndVersion(authentication, name, version, expansions);
    }

    @Override
    public Page<ScriptDto> getAllByNameAndVersion(Authentication authentication, Pageable pageable, String name, long version, List<String> expansions) {
        return scriptDtoRepository.getAllByNameAndVersion(authentication, pageable, name, version, expansions);
    }
}
