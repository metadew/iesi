package io.metadew.iesi.server.rest.connection.dto;

import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.ConnectionParameter;
import io.metadew.iesi.server.rest.connection.ConnectionsController;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class ConnectionDtoResourceAssembler extends RepresentationModelAssemblerSupport<Connection, ConnectionDto> {

    private ModelMapper modelMapper;

    public ConnectionDtoResourceAssembler() {
        super(ConnectionsController.class, ConnectionDto.class);
        this.modelMapper = new ModelMapper();
    }

    @Override
    public ConnectionDto toModel(Connection connection) {
        return new ConnectionDto(
                connection.getMetadataKey().getName(),
                connection.getType(),
                connection.getDescription(),
                toConnectionEnvironmentDto(connection.getParameters())
        );
    }

    public Set<ConnectionEnvironmentDto> toConnectionEnvironmentDto(List<ConnectionParameter> parameters) {
        Set<ConnectionParameterDto> connectionParameterDtos = new HashSet<>();
        String environment = null;
        for (ConnectionParameter connectionParameter : parameters) {
            if (environment == null) {
                environment = connectionParameter.getMetadataKey().getConnectionKey().getEnvironmentKey().getName();
            }
            connectionParameterDtos.add(new ConnectionParameterDto(connectionParameter.getName(),
                    connectionParameter.getValue()));
        }
        return Stream.of(new ConnectionEnvironmentDto(environment, connectionParameterDtos)).collect(Collectors.toSet());
    }

    public List<ConnectionDto> toModel(List<Connection> connections) {
        return connections.stream().map(this::toModel).collect(Collectors.toList());
    }

    public ConnectionDto convertToDto(ConnectionDto connectionDto) {
        return connectionDto;
    }

    public ConnectionDto toModel(ConnectionDto componentDto) {
        return componentDto;
    }
}
