package io.metadew.iesi.server.rest.connection;

import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;
import io.metadew.iesi.server.rest.connection.dto.ConnectionDto;
import io.metadew.iesi.server.rest.connection.dto.ConnectionDtoResourceAssembler;
import io.metadew.iesi.server.rest.error.DataBadRequestException;
import io.metadew.iesi.server.rest.resource.HalMultipleEmbeddedResource;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static io.metadew.iesi.server.rest.helper.Filter.distinctByKey;

@RestController
@CrossOrigin
@Tag(name = "connections", description = "Everything about connections")
@RequestMapping("/connections")
public class ConnectionsController {

    private ConnectionService connectionService;
    private ConnectionDtoResourceAssembler connectionDtoResourceAssembler;

    @Autowired
    ConnectionsController(ConnectionService connectionService,
                          ConnectionDtoResourceAssembler connectionDtoResourceAssembler) {
        this.connectionDtoResourceAssembler = connectionDtoResourceAssembler;
        this.connectionService = connectionService;
    }

    @GetMapping("")
    @PreAuthorize("hasPrivilege('CONNECTIONS_READ')")
    public HalMultipleEmbeddedResource<ConnectionDto> getAll() {
        List<Connection> connections = connectionService.getAll();
        return new HalMultipleEmbeddedResource<>(connections.stream()
                .filter(distinctByKey(connection -> connection.getMetadataKey().getName()))
                .map(connection -> connectionDtoResourceAssembler.toModel(connection))
                .collect(Collectors.toList()));
    }

    @GetMapping("/{name}")
    @PreAuthorize("hasPrivilege('CONNECTIONS_READ')")
    public HalMultipleEmbeddedResource<ConnectionDto> getByName(@PathVariable String name) {
        List<Connection> connections = connectionService.getByName(name);
        return new HalMultipleEmbeddedResource<>(connections.stream()
                .filter(distinctByKey(connection -> connection.getMetadataKey().getName()))
                .map(connection -> connectionDtoResourceAssembler.toModel(connection))
                .collect(Collectors.toList()));
    }

    @GetMapping("/{name}/{environment}")
    @PreAuthorize("hasPrivilege('CONNECTIONS_READ')")
    public ConnectionDto get(@PathVariable String name, @PathVariable String environment) throws MetadataDoesNotExistException {
        Connection connection = connectionService.getByNameAndEnvironment(name, environment)
                .orElseThrow(() -> new MetadataDoesNotExistException(new ConnectionKey(name, environment)));
        return connectionDtoResourceAssembler.toModel(connection);
    }

    @PostMapping("")
    @PreAuthorize("hasPrivilege('CONNECTIONS_WRITE')")
    public ResponseEntity<ConnectionDto> post(@Valid @RequestBody ConnectionDto connectionDto) {
        try {
            connectionService.createConnection(connectionDto);
            return ResponseEntity.ok(connectionDtoResourceAssembler.toModel(connectionDto.convertToEntity()));
        } catch (MetadataAlreadyExistsException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Connection " + connectionDto.getName() + " already exists");
        }
    }

    @PutMapping("")
    @PreAuthorize("hasPrivilege('CONNECTIONS_WRITE')")
    public HalMultipleEmbeddedResource<ConnectionDto> putAll(@Valid @RequestBody List<ConnectionDto> connectionDtos) throws MetadataDoesNotExistException {
        HalMultipleEmbeddedResource<ConnectionDto> halMultipleEmbeddedResource = new HalMultipleEmbeddedResource<>();
        connectionService.updateConnections(connectionDtos);
        for (ConnectionDto connectionDto : connectionDtos) {
            ConnectionDto updatedConnectionDto = connectionDtoResourceAssembler.toModel(connectionDto.convertToEntity());
            halMultipleEmbeddedResource.embedResource(updatedConnectionDto);
        }
        return halMultipleEmbeddedResource;
    }

    @PutMapping("/{name}/{environment}")
    @PreAuthorize("hasPrivilege('CONNECTIONS_WRITE')")
    public ConnectionDto put(@PathVariable String name, @PathVariable String environment, @RequestBody ConnectionDto connectionDto) throws MetadataDoesNotExistException {
        if (!connectionDto.getName().equals(name) || !connectionDto.getEnvironment().equals(environment)) {
            throw new DataBadRequestException(name);
        }
        connectionService.updateConnection(connectionDto);
        return connectionDtoResourceAssembler.toModel(connectionDto.convertToEntity());
    }

    @DeleteMapping("")
    @PreAuthorize("hasPrivilege('CONNECTIONS_WRITE')")
    public ResponseEntity<?> deleteAll() {
        connectionService.deleteAll();
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{name}")
    @PreAuthorize("hasPrivilege('CONNECTIONS_WRITE')")
    public ResponseEntity<?> deleteByName(@PathVariable String name) throws MetadataDoesNotExistException {
        connectionService.deleteByName(name);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{name}/{environment}")
    @PreAuthorize("hasPrivilege('CONNECTIONS_WRITE')")
    public ResponseEntity<?> delete(@PathVariable String name, @PathVariable String environment) throws MetadataDoesNotExistException {
        connectionService.deleteByNameAndEnvironment(name, environment);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}