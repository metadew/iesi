package io.metadew.iesi.server.rest.controller;

import io.metadew.iesi.metadata.configuration.connection.ConnectionConfiguration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;
import io.metadew.iesi.server.rest.error.DataBadRequestException;
import io.metadew.iesi.server.rest.resource.HalMultipleEmbeddedResource;
import io.metadew.iesi.server.rest.resource.connection.dto.ConnectionByNameDto;
import io.metadew.iesi.server.rest.resource.connection.dto.ConnectionDto;
import io.metadew.iesi.server.rest.resource.connection.dto.ConnectionGlobalDto;
import io.metadew.iesi.server.rest.resource.connection.resource.ConnectionByNameDtoResourceAssembler;
import io.metadew.iesi.server.rest.resource.connection.resource.ConnectionDtoResourceAssembler;
import io.metadew.iesi.server.rest.resource.connection.resource.ConnectionGlobalDtoResourceAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.metadew.iesi.server.rest.helper.Filter.distinctByKey;

@RestController
@Tag(name = "connections", description = "Everything about connections")
@RequestMapping("/connections")
public class ConnectionsController {

    private ConnectionConfiguration connectionConfiguration;
    private ConnectionDtoResourceAssembler connectionDtoResourceAssembler;
    private ConnectionByNameDtoResourceAssembler connectionByNameDtoResourceAssembler;
    private ConnectionGlobalDtoResourceAssembler connectionGlobalDtoResourceAssembler;

    @Autowired
    ConnectionsController(ConnectionConfiguration connectionConfiguration,
                          ConnectionDtoResourceAssembler connectionDtoResourceAssembler, ConnectionByNameDtoResourceAssembler connectionByNameDtoResourceAssembler,
                          ConnectionGlobalDtoResourceAssembler connectionGlobalDtoResourceAssembler) {
        this.connectionConfiguration = connectionConfiguration;
        this.connectionDtoResourceAssembler = connectionDtoResourceAssembler;
        this.connectionByNameDtoResourceAssembler = connectionByNameDtoResourceAssembler;
        this.connectionGlobalDtoResourceAssembler = connectionGlobalDtoResourceAssembler;
    }


    @GetMapping("")
    public HalMultipleEmbeddedResource<ConnectionGlobalDto> getAll() {
        List<Connection> connections = connectionConfiguration.getAll();
        return new HalMultipleEmbeddedResource<>(connections.stream()
                .filter(distinctByKey(Connection::getName))
                .map(connection -> connectionGlobalDtoResourceAssembler.toModel(Collections.singletonList(connection)))
                .collect(Collectors.toList()));
    }

    @GetMapping("/{name}")
    public ConnectionByNameDto getByName(@PathVariable String name) {
        List<Connection> connections = connectionConfiguration.getByName(name);
        return connectionByNameDtoResourceAssembler.toModel(connections);
    }

    @GetMapping("/{name}/{environment}")
    public ConnectionDto get(@PathVariable String name, @PathVariable String environment) throws MetadataDoesNotExistException {
        Optional<Connection> connection = connectionConfiguration.get(new ConnectionKey(name, environment));
        return connection
                .map(connectionDtoResourceAssembler::toModel)
                .orElseThrow(() -> new MetadataDoesNotExistException(new ConnectionKey(name, environment)));
    }

    @PostMapping("")
    public ResponseEntity<ConnectionDto> post(@Valid @RequestBody ConnectionDto connectionDto) {
        try {
            connectionConfiguration.insert(connectionDto.convertToEntity());
            return ResponseEntity.ok(connectionDtoResourceAssembler.toModel(connectionDto.convertToEntity()));
        } catch (MetadataAlreadyExistsException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Connection " + connectionDto.getName() + " already exists");
        }
    }

    @PutMapping("")
    public HalMultipleEmbeddedResource<ConnectionDto> putAll(@Valid @RequestBody List<ConnectionDto> connectionDtos) throws MetadataDoesNotExistException {
        HalMultipleEmbeddedResource<ConnectionDto> halMultipleEmbeddedResource = new HalMultipleEmbeddedResource<>();
        for (ConnectionDto connectionDto : connectionDtos) {
            connectionConfiguration.update(connectionDto.convertToEntity());
            ConnectionDto updatedConnectionDto = connectionDtoResourceAssembler.toModel(connectionDto.convertToEntity());
            halMultipleEmbeddedResource.embedResource(updatedConnectionDto);
        }
        return halMultipleEmbeddedResource;
    }

    @PutMapping("/{name}/{environment}")
    public ConnectionDto put(@PathVariable String name, @PathVariable String environment, @RequestBody ConnectionDto connectionDto) throws MetadataDoesNotExistException {
        if (!connectionDto.getName().equals(name) || !connectionDto.getEnvironment().equals(environment)) {
            throw new DataBadRequestException(name);
        }
        connectionConfiguration.update(connectionDto.convertToEntity());
        return connectionDtoResourceAssembler.toModel(connectionDto.convertToEntity());
    }

    @DeleteMapping("")
    public ResponseEntity<?> deleteAll() {
        List<Connection> connections = connectionConfiguration.getAll();
        if (!connections.isEmpty()) {
            connectionConfiguration.deleteAll();
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<?> deleteByName(@PathVariable String name) throws MetadataDoesNotExistException {
        connectionConfiguration.deleteByName(name);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{name}/{environment}")
    public ResponseEntity<?> delete(@PathVariable String name, @PathVariable String environment) throws MetadataDoesNotExistException {
        connectionConfiguration.delete(new ConnectionKey(name, environment));
        return ResponseEntity.status(HttpStatus.OK).build();

    }

}