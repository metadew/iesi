package io.metadew.iesi.server.rest.resource.connection.resource;

import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.server.rest.controller.ConnectionsController;
import io.metadew.iesi.server.rest.resource.connection.dto.ConnectionGlobalDto;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ConnectionGlobalDtoResourceAssembler extends RepresentationModelAssemblerSupport<List<Connection>, ConnectionGlobalDto> {

    private final ModelMapper modelMapper;

    public ConnectionGlobalDtoResourceAssembler() {
        super(ConnectionsController.class, ConnectionGlobalDto.class);
        this.modelMapper = new ModelMapper();
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
        return modelMapper.map(connections.get(0), ConnectionGlobalDto.class);

    }
}
