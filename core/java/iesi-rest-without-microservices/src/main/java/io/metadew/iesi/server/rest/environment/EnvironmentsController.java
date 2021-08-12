package io.metadew.iesi.server.rest.environment;

import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.environment.Environment;
import io.metadew.iesi.metadata.definition.environment.key.EnvironmentKey;
import io.metadew.iesi.server.rest.connection.ConnectionService;
import io.metadew.iesi.server.rest.connection.dto.ConnectionDtoResourceAssembler;
import io.metadew.iesi.server.rest.environment.dto.EnvironmentDto;
import io.metadew.iesi.server.rest.environment.dto.EnvironmentDtoResourceAssembler;
import io.metadew.iesi.server.rest.environment.dto.IEnvironmentDtoService;
import io.metadew.iesi.server.rest.error.DataBadRequestException;
import io.metadew.iesi.server.rest.resource.HalMultipleEmbeddedResource;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static io.metadew.iesi.server.rest.helper.Filter.distinctByKey;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@Tag(name = "environments", description = "Everything about environments")
@RequestMapping("/environments")
public class EnvironmentsController {

    private EnvironmentService environmentService;
    private ConnectionService connectionService;
    private EnvironmentDtoResourceAssembler environmentDtoResourceAssembler;
    private ConnectionDtoResourceAssembler connectionDtoResourceAssembler;
    private IEnvironmentDtoService environmentDtoService;
    private PagedResourcesAssembler<EnvironmentDto> environmentDtoPagedResourcesAssembler;

    @Autowired
    EnvironmentsController(EnvironmentService environmentService, ConnectionService connectionService,
                           EnvironmentDtoResourceAssembler environmentDtoResourceAssembler, ConnectionDtoResourceAssembler connectionDtoResourceAssembler,
                           IEnvironmentDtoService environmentDtoService, PagedResourcesAssembler<EnvironmentDto> environmentDtoPagedResourcesAssembler ) {
        this.environmentService = environmentService;
        this.connectionService = connectionService;
        this.environmentDtoResourceAssembler = environmentDtoResourceAssembler;
        this.connectionDtoResourceAssembler = connectionDtoResourceAssembler;
        this.environmentDtoService = environmentDtoService;
        this.environmentDtoPagedResourcesAssembler = environmentDtoPagedResourcesAssembler;
    }

    @GetMapping("")
    @PreAuthorize("hasPrivilege('ENVIRONMENTS_READ')")
    public PagedModel<EnvironmentDto> getAll(Pageable pageable) {
        Page<EnvironmentDto> environmentDtoPage = environmentDtoService.getAll(pageable);

        if (environmentDtoPage.hasContent())
            return environmentDtoPagedResourcesAssembler.toModel(environmentDtoPage, environmentDtoResourceAssembler::toModel);
        return (PagedModel<EnvironmentDto>) environmentDtoPagedResourcesAssembler.toEmptyModel(environmentDtoPage, EnvironmentDto.class);
    }

    @GetMapping("/list")
    @PreAuthorize("hasPrivilege('ENVIRONMENTS_READ')")
    public HalMultipleEmbeddedResource<EnvironmentDto> getAll() {
        List<Environment> environments = environmentService.getAll();
        return new HalMultipleEmbeddedResource<>(
                environments.stream().filter(distinctByKey(Environment::getName))
                        .map(environment -> environmentDtoResourceAssembler.toModel(environment))
                        .collect(Collectors.toList()));
    }

    @GetMapping("/{name}")
    @PreAuthorize("hasPrivilege('ENVIRONMENTS_READ')")
    public EnvironmentDto getByName(@PathVariable String name) throws MetadataDoesNotExistException {
        return environmentService.getByName(name)
                .map(environmentDtoResourceAssembler::toModel)
                .orElseThrow(() -> new MetadataDoesNotExistException(new EnvironmentKey(name)));
    }

    @PostMapping("")
    @PreAuthorize("hasPrivilege('ENVIRONMENTS_WRITE')")
    public EnvironmentDto post(@RequestBody EnvironmentDto environment) throws MetadataAlreadyExistsException {
        environmentService.createEnvironment(environment);
        return environmentDtoResourceAssembler.toModel(environment.convertToEntity());
    }

    @PutMapping("")
    @PreAuthorize("hasPrivilege('ENVIRONMENTS_WRITE')")
    public HalMultipleEmbeddedResource<EnvironmentDto> putAll(@Valid @RequestBody List<EnvironmentDto> environmentDtos) throws MetadataDoesNotExistException {
        environmentService.updateEnvironments(environmentDtos);
        HalMultipleEmbeddedResource<EnvironmentDto> halMultipleEmbeddedResource = new HalMultipleEmbeddedResource<>();
        for (EnvironmentDto environmentDto : environmentDtos) {
            halMultipleEmbeddedResource.embedResource(environmentDto);
            halMultipleEmbeddedResource.add(linkTo(methodOn(EnvironmentsController.class)
                    .getByName(environmentDto.getName()))
                    .withRel(environmentDto.getName()));
        }

        return halMultipleEmbeddedResource;
    }

    @PutMapping("/{name}")
    @PreAuthorize("hasPrivilege('ENVIRONMENTS_WRITE')")
    public EnvironmentDto put(@PathVariable String name, @RequestBody EnvironmentDto environment) throws MetadataDoesNotExistException {
        if (!environment.getName().equals(name)) {
            throw new DataBadRequestException(name);
        } else if (environment.getName() == null) {
            throw new DataBadRequestException(null);
        }
        environmentService.updateEnvironment(environment);
        return environmentDtoResourceAssembler.toModel(environment.convertToEntity());

    }

    @GetMapping("/{name}/connections")
    @PreAuthorize("hasPrivilege('CONNECTIONS_READ')")
    public HalMultipleEmbeddedResource getConnections(@PathVariable String name) {
        List<Connection> result = connectionService.getByEnvironment(name);
        return new HalMultipleEmbeddedResource<>(result.stream()
                .map(connectionDtoResourceAssembler::toModel)
                .collect(Collectors.toList()));

    }

    @DeleteMapping("")
    @PreAuthorize("hasPrivilege('ENVIRONMENTS_WRITE')")
    public ResponseEntity<?> deleteAll() {
        environmentService.deleteAll();
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{name}")
    @PreAuthorize("hasPrivilege('ENVIRONMENTS_WRITE')")
    public ResponseEntity<?> delete(@PathVariable String name) throws MetadataDoesNotExistException {
        environmentService.deleteByName(name);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}