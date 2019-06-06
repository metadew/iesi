package io.metadew.iesi.server.rest.ressource.connection.resource;

import io.metadew.iesi.metadata.definition.Connection;
import io.metadew.iesi.server.rest.controller.ConnectionsController;
import io.metadew.iesi.server.rest.ressource.connection.dto.ConnectionGlobalDto;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.List;

import static io.metadew.iesi.server.rest.helper.Filter.distinctByKey;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class ConnectionGlobalDtoResourceAssembler extends ResourceAssemblerSupport<List<Connection>, ConnectionGlobalDto> {

    private final ModelMapper modelMapper;

    public ConnectionGlobalDtoResourceAssembler() {
        super(ConnectionsController.class, ConnectionGlobalDto.class);
        this.modelMapper = new ModelMapper();
    }

    @Override
    public ConnectionGlobalDto toResource(List<Connection> connections) {
        ConnectionGlobalDto connectionGlobalDto = convertToDto(connections);
        connectionGlobalDto.add(linkTo(methodOn(ConnectionsController.class)
                .getByName(connectionGlobalDto.getName()))
                .withSelfRel());
        return connectionGlobalDto;
    }

    private ConnectionGlobalDto convertToDto(List<Connection> connections) {
        if (connections.isEmpty()) {
            throw new IllegalArgumentException("Cannot create Connection global DTO from empty list");
        }
        if (connections.stream().filter(distinctByKey(Connection::getName)).count() > 1) {
            throw new IllegalArgumentException("Cannot create Connection global DTO from list with multiple connection names");
        }
        return modelMapper.map(connections.get(0), ConnectionGlobalDto.class);
//        return connections.stream()
//                .filter(distinctByKey(Connection::getName))
//                .map(connection -> modelMapper.map(connection, ConnectionGlobalDto.class))
//                .collect(Collectors.toList());

    }
}
