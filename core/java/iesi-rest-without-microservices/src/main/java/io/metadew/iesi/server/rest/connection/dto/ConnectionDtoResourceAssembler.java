package io.metadew.iesi.server.rest.connection.dto;

import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.ConnectionParameter;
import io.metadew.iesi.server.rest.connection.ConnectionsController;
import io.metadew.iesi.server.rest.environment.EnvironmentsController;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ConnectionDtoResourceAssembler extends RepresentationModelAssemblerSupport<Connection, ConnectionDto> {

    private ModelMapper modelMapper;

    public ConnectionDtoResourceAssembler() {
        super(ConnectionsController.class, ConnectionDto.class);
        this.modelMapper = new ModelMapper();
    }

    @Override
    public ConnectionDto toModel(Connection connection) {
        ConnectionDto connectionDto = convertToDto(connection);
        Link selfLink = linkTo(methodOn(ConnectionsController.class)
                .get(connection.getMetadataKey().getName(), connection.getMetadataKey().getEnvironmentKey().getName()))
                .withSelfRel();
        connectionDto.add(selfLink);
        Link environmentLink = linkTo(methodOn(EnvironmentsController.class)
                .getByName(connection.getMetadataKey().getEnvironmentKey().getName()))
                .withRel("environment");
        connectionDto.add(environmentLink);
        return connectionDto;
    }

    private ConnectionDto convertToDto(Connection connection) {
        return new ConnectionDto(connection.getMetadataKey().getName(),
                connection.getType(),
                connection.getDescription(),
                connection.getMetadataKey().getEnvironmentKey().getName(),
                connection.getParameters().stream().map(this::convertToDto).collect(Collectors.toList()));
    }

    private ConnectionParameterDto convertToDto(ConnectionParameter connectionParameter) {
        return new ConnectionParameterDto(connectionParameter.getName(), connectionParameter.getValue());
    }

}
