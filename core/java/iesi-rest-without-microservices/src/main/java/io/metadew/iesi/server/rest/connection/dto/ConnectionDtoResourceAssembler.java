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
        throw new RuntimeException("Unsupported operation");
    }

    public ConnectionDto convertToDto(ConnectionDto connectionDto) {
        return connectionDto;
    }

    public ConnectionDto toModel(ConnectionDto componentDto) {
        return componentDto;
    }
}
