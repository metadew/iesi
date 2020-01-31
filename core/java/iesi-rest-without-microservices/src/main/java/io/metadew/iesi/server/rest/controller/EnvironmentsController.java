package io.metadew.iesi.server.rest.controller;

import io.metadew.iesi.metadata.configuration.connection.ConnectionConfiguration;
import io.metadew.iesi.metadata.configuration.environment.EnvironmentConfiguration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.environment.Environment;
import io.metadew.iesi.metadata.definition.environment.key.EnvironmentKey;
import io.metadew.iesi.server.rest.error.DataBadRequestException;
import io.metadew.iesi.server.rest.resource.HalMultipleEmbeddedResource;
import io.metadew.iesi.server.rest.resource.connection.resource.ConnectionDtoResourceAssembler;
import io.metadew.iesi.server.rest.resource.environment.dto.EnvironmentDto;
import io.metadew.iesi.server.rest.resource.environment.resource.EnvironmentDtoResourceAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static io.metadew.iesi.server.rest.helper.Filter.distinctByKey;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping("/environments")
public class EnvironmentsController {

    private EnvironmentConfiguration environmentConfiguration;
    private ConnectionConfiguration connectionConfiguration;
    private EnvironmentDtoResourceAssembler environmentDtoResourceAssembler;
    private ConnectionDtoResourceAssembler connectionDtoResourceAssembler;

    @Autowired
    EnvironmentsController(EnvironmentConfiguration environmentConfiguration, ConnectionConfiguration connectionConfiguration,
                           EnvironmentDtoResourceAssembler environmentDtoResourceAssembler, ConnectionDtoResourceAssembler connectionDtoResourceAssembler) {
        this.environmentConfiguration = environmentConfiguration;
        this.connectionConfiguration = connectionConfiguration;
        this.environmentDtoResourceAssembler = environmentDtoResourceAssembler;
        this.connectionDtoResourceAssembler = connectionDtoResourceAssembler;
    }

    @GetMapping("")
    public HalMultipleEmbeddedResource<EnvironmentDto> getAll() {
        List<Environment> environments = environmentConfiguration.getAll();
        return new HalMultipleEmbeddedResource<EnvironmentDto>(
                environments.stream().filter(distinctByKey(Environment::getName))
                        .map(environment -> environmentDtoResourceAssembler.toResource(environment))
                        .collect(Collectors.toList()));
    }

    @GetMapping("/{name}")
    public EnvironmentDto getByName(@PathVariable String name) throws MetadataDoesNotExistException {
        return environmentConfiguration.get(new EnvironmentKey(name))
                .map(environment -> environmentDtoResourceAssembler.toResource(environment))
                .orElseThrow(() -> new MetadataDoesNotExistException(new EnvironmentKey(name)));
    }

    //
    @PostMapping("")
    public EnvironmentDto post(@Valid @RequestBody EnvironmentDto environment) throws MetadataAlreadyExistsException {
        environmentConfiguration.insert(environment.convertToEntity());
        return environmentDtoResourceAssembler.toResource(environment.convertToEntity());
    }

    @PutMapping("")
    public HalMultipleEmbeddedResource<EnvironmentDto> putAll(@Valid @RequestBody List<EnvironmentDto> environmentDtos) throws MetadataDoesNotExistException {
        HalMultipleEmbeddedResource<EnvironmentDto> halMultipleEmbeddedResource = new HalMultipleEmbeddedResource<>();
        for (EnvironmentDto environmentDto : environmentDtos) {
            environmentConfiguration.update(environmentDto.convertToEntity());
            halMultipleEmbeddedResource.embedResource(environmentDto);
            halMultipleEmbeddedResource.add(linkTo(methodOn(EnvironmentsController.class)
                    .getByName(environmentDto.getName()))
                    .withRel(environmentDto.getName()));
        }

        return halMultipleEmbeddedResource;
    }

    @PutMapping("/{name}")
    public EnvironmentDto put(@PathVariable String name, @RequestBody EnvironmentDto environment) throws MetadataDoesNotExistException {
        if (!environment.getName().equals(name)) {
            throw new DataBadRequestException(name);
        } else if (environment.getName() == null) {
            throw new DataBadRequestException(null);
        }
        environmentConfiguration.update(environment.convertToEntity());
        return environmentDtoResourceAssembler.toResource(environment.convertToEntity());

    }

    @GetMapping("/{name}/connections")
    public HalMultipleEmbeddedResource getConnections(@PathVariable String name) {
        List<Connection> result = connectionConfiguration.getByEnvironment(name);
        return new HalMultipleEmbeddedResource<>(result.stream()
                .map(connectionDtoResourceAssembler::toResource)
                .collect(Collectors.toList()));

    }

    @DeleteMapping("")
    public ResponseEntity<?> deleteAll() {
        environmentConfiguration.deleteAll();
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<?> delete(@PathVariable String name) throws MetadataDoesNotExistException {
        environmentConfiguration.delete(new EnvironmentKey(name));
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}