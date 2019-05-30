package io.metadew.iesi.server.rest.ressource.connection.resource;

import io.metadew.iesi.metadata.definition.Connection;
import io.metadew.iesi.server.rest.controller.ConnectionsController;
import io.metadew.iesi.server.rest.ressource.HalSingleEmbeddedResource;
import io.metadew.iesi.server.rest.ressource.connection.dto.ConnectionDto;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class ConnectionDtoResourceAssembler extends ResourceAssemblerSupport<Connection, HalSingleEmbeddedResource> {

    private ModelMapper modelMapper;

    public ConnectionDtoResourceAssembler() {
        super(ConnectionsController.class, HalSingleEmbeddedResource.class);
        this.modelMapper = new ModelMapper();
    }

    @Override
    public HalSingleEmbeddedResource<ConnectionDto> toResource(Connection connection) {
        ConnectionDto connectionDto = convertToDto(connection);
        HalSingleEmbeddedResource<ConnectionDto> halSingleEmbeddedResource = new HalSingleEmbeddedResource<>();
        halSingleEmbeddedResource.setEmbeddedResource(connectionDto);
        Link link = linkTo(methodOn(ConnectionsController.class).getByNameandEnvironment(connection.getName(), connection.getEnvironment())).withSelfRel();
        halSingleEmbeddedResource.add(link);
        return halSingleEmbeddedResource;
    }

    private ConnectionDto convertToDto(Connection connection) {
        return modelMapper.map(connection, ConnectionDto.class);
    }
}
