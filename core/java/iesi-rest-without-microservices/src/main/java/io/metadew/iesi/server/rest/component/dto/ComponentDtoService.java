package io.metadew.iesi.server.rest.component.dto;

import io.metadew.iesi.metadata.configuration.security.SecurityGroupConfiguration;
import io.metadew.iesi.metadata.definition.component.Component;
import io.metadew.iesi.metadata.definition.component.key.ComponentKey;
import io.metadew.iesi.metadata.definition.security.SecurityGroup;
import io.metadew.iesi.metadata.tools.IdentifierTools;
import io.metadew.iesi.server.rest.component.ComponentFilter;
import io.metadew.iesi.server.rest.component.IComponentDtoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@ConditionalOnWebApplication
public class ComponentDtoService implements IComponentDtoService {

    private final IComponentDtoRepository componentDtoRepository;
    private final SecurityGroupConfiguration securityGroupConfiguration;

    @Autowired
    public ComponentDtoService(IComponentDtoRepository componentDtoRepository, SecurityGroupConfiguration securityGroupConfiguration) {
        this.componentDtoRepository = componentDtoRepository;
        this.securityGroupConfiguration = securityGroupConfiguration;
    }

    @Override
    public Page<ComponentDto> getAll(Authentication authentication, Pageable pageable, List<ComponentFilter> componentFilters) {
        return componentDtoRepository.getAll(authentication, pageable, componentFilters);
    }

    @Override
    public Page<ComponentDto> getByName(Authentication authentication, Pageable pageable, String name) {
        return componentDtoRepository.getByName(authentication, pageable, name);
    }

    @Override
    public Optional<ComponentDto> getByNameAndVersion(Authentication authentication, String name, long version) {
        return componentDtoRepository.getByNameAndVersion(authentication, name, version);
    }

    @Override
    public Component convertToEntity(ComponentDto componentDto) {
        SecurityGroup securityGroup = securityGroupConfiguration.getByName(componentDto.getSecurityGroupName())
                .orElseThrow(() -> new RuntimeException("Could not find security group with name " + componentDto.getSecurityGroupName()));
        return new Component(
                new ComponentKey(IdentifierTools.getComponentIdentifier(componentDto.getName()), componentDto.getVersion().getNumber()),
                securityGroup.getMetadataKey(),
                componentDto.getSecurityGroupName(),
                componentDto.getType(),
                componentDto.getName(),
                componentDto.getDescription(),
                componentDto.getVersion().convertToEntity(IdentifierTools.getComponentIdentifier(componentDto.getName())),
                componentDto.getParameters().stream()
                        .map(parameter -> parameter.convertToEntity(IdentifierTools.getComponentIdentifier(componentDto.getName()), componentDto.getVersion().getNumber()))
                        .collect(Collectors.toList()),
                componentDto.getAttributes().stream()
                        .map(attribute -> attribute.convertToEntity(IdentifierTools.getComponentIdentifier(componentDto.getName()), componentDto.getVersion().getNumber()))
                        .collect(Collectors.toList()));
    }

}
