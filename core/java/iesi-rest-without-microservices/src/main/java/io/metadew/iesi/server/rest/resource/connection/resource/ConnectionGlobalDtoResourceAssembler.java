package io.metadew.iesi.server.rest.resource.connection.resource;

import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.server.rest.controller.ConnectionsController;
import io.metadew.iesi.server.rest.resource.connection.dto.ConnectionGlobalDto;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ConnectionGlobalDtoResourceAssembler extends RepresentationModelAssemblerSupport<List<Connection>, ConnectionGlobalDto> {


    public ConnectionGlobalDtoResourceAssembler() {
        super(ConnectionsController.class, ConnectionGlobalDto.class);
    }

    @Override
    public ConnectionGlobalDto toModel(List<Connection> connections) {
        ConnectionGlobalDto connectionGlobalDto = convertToDto(connections);
        connectionGlobalDto.add(linkTo(methodOn(ConnectionsController.class)
                .getByName(connectionGlobalDto.getName()))
                .withSelfRel());
        return connectionGlobalDto;
    }

    private ConnectionGlobalDto convertToDto(List<Connection> connections) {
        return new ConnectionGlobalDto(connections.get(0).getMetadataKey().getName(), connections.get(0).getType(), connections.get(0).getDescription());

    }
}
