package io.metadew.iesi.server.rest.ressource.connection.resource;

import io.metadew.iesi.metadata.definition.Connection;
import io.metadew.iesi.server.rest.controller.ConnectionsController;
import io.metadew.iesi.server.rest.ressource.HalMultipleEmbeddedResource;
import io.metadew.iesi.server.rest.ressource.connection.dto.ConnectionGlobalDto;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static io.metadew.iesi.server.rest.helper.Filter.distinctByKey;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class ConnectionGlobalDtoResourceAssembler extends ResourceAssemblerSupport<List<Connection>, HalMultipleEmbeddedResource> {

    private final ModelMapper modelMapper;

    public ConnectionGlobalDtoResourceAssembler() {
        super(ConnectionsController.class, HalMultipleEmbeddedResource.class);
        this.modelMapper = new ModelMapper();
    }

    @Override
    public HalMultipleEmbeddedResource<ConnectionGlobalDto> toResource(List<Connection> connections) {
        List<ConnectionGlobalDto> connectionGlobalDtos = convertToDto(connections);
        HalMultipleEmbeddedResource<ConnectionGlobalDto> halMultipleEmbeddedResource = new HalMultipleEmbeddedResource<>();
        for (ConnectionGlobalDto connectionGlobalDto : connectionGlobalDtos) {
            halMultipleEmbeddedResource.embedResource(connectionGlobalDto);
            halMultipleEmbeddedResource.add(linkTo(methodOn(ConnectionsController.class)
                    .getByName(connectionGlobalDto.getName()))
                    .withRel("connection:"+connectionGlobalDto.getName()));
        }
        return halMultipleEmbeddedResource;
    }

    private List<ConnectionGlobalDto> convertToDto(List<Connection> connections) {
        return connections.stream()
                .filter(distinctByKey(Connection::getName))
                .map(connection -> modelMapper.map(connection, ConnectionGlobalDto.class))
                .collect(Collectors.toList());
    }
}
