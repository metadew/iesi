//package io.metadew.iesi.server.rest.connection.resource;
//
//import io.metadew.iesi.metadata.definition.connection.Connection;
//import io.metadew.iesi.server.rest.connection.ConnectionsController;
//import io.metadew.iesi.server.rest.connection.dto.ConnectionByNameDto;
//import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
//import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
//
//@Component
//public class ConnectionByNameDtoResourceAssembler extends RepresentationModelAssemblerSupport<List<Connection>, ConnectionByNameDto> {
//
//    public ConnectionByNameDtoResourceAssembler() {
//        super(ConnectionsController.class, ConnectionByNameDto.class);
//    }
//
//    @Override
//    public ConnectionByNameDto toModel(List<Connection> connections) {
//        ConnectionByNameDto connectionByNameDto = convertToDto(connections);
//        for (String environment : connectionByNameDto.getEnvironments()) {
//            connectionByNameDto.add(
//                    linkTo(methodOn(ConnectionsController.class).get(connectionByNameDto.getName(), environment))
//                    .withRel("connection:"+connectionByNameDto.getName()+"-"+environment));
//        }
//        return connectionByNameDto;
//    }
//
//    private ConnectionByNameDto convertToDto(List<Connection> connections) {
//        return new ConnectionByNameDto(
//                connections.get(0).getMetadataKey().getName(),
//                connections.get(0).getType(),
//                connections.get(0).getDescription(),
//                connections.stream()
//                        .map(connection -> connection.getMetadataKey().getEnvironmentKey().getName())
//                        .collect(Collectors.toList()));
//    }
//}
