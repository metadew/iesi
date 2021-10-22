package io.metadew.iesi.server.rest.connection.dto;

import io.metadew.iesi.metadata.configuration.security.SecurityGroupConfiguration;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.security.SecurityGroup;
import io.metadew.iesi.server.rest.connection.ConnectionFilter;
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
public class ConnectionDtoService implements IConnectionDtoService {

    private final ConnectionDtoRepository connectionDtoRepository;

    @Autowired
    public ConnectionDtoService(ConnectionDtoRepository connectionDtoRepository) {
        this.connectionDtoRepository = connectionDtoRepository;
    }

    @Override
    public Page<ConnectionDto> getAll(Authentication authentication, Pageable pageable, List<ConnectionFilter> componentFilters) {
        return connectionDtoRepository.getAll(authentication, pageable, componentFilters);
    }

    @Override
    public Optional<ConnectionDto> getByName(Authentication authentication, String name) {
        return connectionDtoRepository.getByName(authentication, name);
    }

    @Override
    public List<Connection> convertToEntity(ConnectionDto connectionDto) {
        SecurityGroup securityGroup = SecurityGroupConfiguration.getInstance().getByName(connectionDto.getSecurityGroupName())
                .orElseThrow(() -> new RuntimeException("could not find Security Group with name " + connectionDto.getSecurityGroupName()));
        return connectionDto.getEnvironments().stream()
                .map(environment -> new Connection(
                        connectionDto.getName(),
                        securityGroup.getMetadataKey(),
                        connectionDto.getSecurityGroupName(),
                        connectionDto.getType(),
                        connectionDto.getDescription(),
                        environment.getEnvironment(),
                        environment.getParameters().stream()
                                .map(parameter -> parameter.convertToEntity(connectionDto.getName(), environment.getEnvironment()))
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
    }
}
