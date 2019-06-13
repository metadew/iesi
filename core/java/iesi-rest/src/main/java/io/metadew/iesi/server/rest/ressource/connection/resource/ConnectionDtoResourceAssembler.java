package io.metadew.iesi.server.rest.ressource.connection.resource;

import io.metadew.iesi.metadata.definition.Connection;
import io.metadew.iesi.server.rest.controller.ConnectionsController;
import io.metadew.iesi.server.rest.controller.EnvironmentsController;
import io.metadew.iesi.server.rest.ressource.connection.dto.ConnectionDto;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class ConnectionDtoResourceAssembler extends ResourceAssemblerSupport<Connection, ConnectionDto> {

    private ModelMapper modelMapper;

    public ConnectionDtoResourceAssembler() {
        super(ConnectionsController.class, ConnectionDto.class);
        this.modelMapper = new ModelMapper();
    }

    @Override
    public ConnectionDto toResource(Connection connection) {
        ConnectionDto connectionDto = convertToDto(connection);
        Link selfLink = linkTo(methodOn(ConnectionsController.class).getByNameandEnvironment(connection.getName(), connection.getEnvironment()))
                .withSelfRel();
        connectionDto.add(selfLink);
        Link environmentLink = linkTo(methodOn(ConnectionsController.class).getByName(connection.getEnvironment()))
                .withRel("environment");
        connectionDto.add(environmentLink);
        return connectionDto;
    }

    private ConnectionDto convertToDto(Connection connection) {
        return modelMapper.map(connection, ConnectionDto.class);
    }
}
