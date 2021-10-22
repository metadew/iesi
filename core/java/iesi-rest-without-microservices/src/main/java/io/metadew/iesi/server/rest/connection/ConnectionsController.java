package io.metadew.iesi.server.rest.connection;

import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;
import io.metadew.iesi.metadata.service.user.IESIPrivilege;
import io.metadew.iesi.server.rest.configuration.security.IesiSecurityChecker;
import io.metadew.iesi.server.rest.connection.dto.ConnectionDto;
import io.metadew.iesi.server.rest.connection.dto.ConnectionDtoResourceAssembler;
import io.metadew.iesi.server.rest.connection.dto.ConnectionDtoService;
import io.metadew.iesi.server.rest.error.DataBadRequestException;
import io.metadew.iesi.server.rest.resource.HalMultipleEmbeddedResource;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@Tag(name = "connections", description = "Everything about connections")
@RequestMapping("/connections")
@ConditionalOnWebApplication
public class ConnectionsController {

    private final ConnectionService connectionService;
    private final ConnectionDtoService connectionDtoService;
    private final ConnectionDtoResourceAssembler connectionDtoResourceAssembler;
    private final PagedResourcesAssembler<ConnectionDto> connectionDtoPagedResourcesAssembler;
    private final IesiSecurityChecker iesiSecurityChecker;

    @Autowired
    ConnectionsController(ConnectionService connectionService,
                          ConnectionDtoService connectionDtoService,
                          ConnectionDtoResourceAssembler connectionDtoResourceAssembler,
                          PagedResourcesAssembler<ConnectionDto> connectionDtoPagedResourcesAssembler,
                          IesiSecurityChecker iesiSecurityChecker) {
        this.connectionService = connectionService;
        this.connectionDtoService = connectionDtoService;
        this.connectionDtoResourceAssembler = connectionDtoResourceAssembler;
        this.connectionDtoPagedResourcesAssembler = connectionDtoPagedResourcesAssembler;
        this.iesiSecurityChecker = iesiSecurityChecker;
    }

    @GetMapping("")
    @PreAuthorize("hasPrivilege('CONNECTIONS_READ')")
    public PagedModel<ConnectionDto> getAll(Pageable pageable, @RequestParam(required = false, name = "name") String name) {
        List<ConnectionFilter> connectionFilters = extractConnectionFilterOptions(name);
        Page<ConnectionDto> connectionDtoPage = connectionDtoService
                .getAll(SecurityContextHolder.getContext().getAuthentication(),
                        pageable,
                        connectionFilters);

        if (connectionDtoPage.hasContent()) {
            return connectionDtoPagedResourcesAssembler.toModel(connectionDtoPage, connectionDtoResourceAssembler::toModel);
        }
        return (PagedModel<ConnectionDto>) connectionDtoPagedResourcesAssembler.toEmptyModel(connectionDtoPage, ConnectionDto.class);
    }


    private List<ConnectionFilter> extractConnectionFilterOptions(String name) {
        List<ConnectionFilter> componentFilters = new ArrayList<>();
        if (name != null) {
            componentFilters.add(new ConnectionFilter(ConnectionFilterOption.NAME, name, false));
        }
        return componentFilters;
    }

    @GetMapping("/{name}")
    @PreAuthorize("hasPrivilege('CONNECTIONS_READ')")
    public ConnectionDto getByName(@PathVariable String name) {
        ConnectionDto connection = connectionDtoService.getByName(
                        SecurityContextHolder.getContext().getAuthentication(),
                        name)
                .orElseThrow(() -> new MetadataDoesNotExistException(
                        new ConnectionKey(name, "")
                ));
        return connectionDtoResourceAssembler.toModel(connection);
    }

    @PostMapping("")
    @PreAuthorize("hasPrivilege('CONNECTIONS_WRITE', #connectionDto.securityGroupName)")
    public ResponseEntity<ConnectionDto> post(@Valid @RequestBody ConnectionDto connectionDto) {
        try {
            connectionService.createConnection(connectionDto);
            return ResponseEntity.ok(connectionDtoResourceAssembler.toModel(connectionDto));
        } catch (MetadataAlreadyExistsException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Connection " + connectionDto.getName() + " already exists");
        }
    }

    @PutMapping("")
    @PreAuthorize("hasPrivilege('CONNECTIONS_WRITE', #connectionDtos.![securityGroupName])")
    public HalMultipleEmbeddedResource<ConnectionDto> putAll(@Valid @RequestBody List<ConnectionDto> connectionDtos) throws MetadataDoesNotExistException {
        HalMultipleEmbeddedResource<ConnectionDto> halMultipleEmbeddedResource = new HalMultipleEmbeddedResource<>();
        connectionService.updateConnections(connectionDtos);
        for (ConnectionDto connectionDto : connectionDtos) {
            ConnectionDto updatedConnectionDto = connectionDtoResourceAssembler.toModel(connectionDto);
            halMultipleEmbeddedResource.embedResource(updatedConnectionDto);
        }
        return halMultipleEmbeddedResource;
    }

    @PutMapping("/{name}")
    @PreAuthorize("hasPrivilege('CONNECTIONS_WRITE', #connectionDto.securityGroupName)")
    public ConnectionDto put(@PathVariable String name, @RequestBody ConnectionDto connectionDto) throws MetadataDoesNotExistException {
        if (!connectionDto.getName().equals(name)) {
            throw new DataBadRequestException(name);
        }
        connectionService.updateConnection(connectionDto);
        return connectionDtoResourceAssembler.toModel(connectionDto);
    }

    @DeleteMapping("/{name}")
    @PreAuthorize("hasPrivilege('CONNECTIONS_WRITE')")
    public ResponseEntity<?> deleteByName(@PathVariable String name) throws MetadataDoesNotExistException {
        ConnectionDto connectionDto = connectionDtoService.getByName(null, name)
                .orElseThrow(() -> new MetadataDoesNotExistException(new ConnectionKey(name, "")));

        if (!iesiSecurityChecker.hasPrivilege(SecurityContextHolder.getContext().getAuthentication(),
                IESIPrivilege.CONNECTIONS_MODIFY.getPrivilege(),
                connectionDto.getSecurityGroupName())
        ) {
            throw new AccessDeniedException("User is not allowed to delete this connection");
        }
        connectionService.deleteByName(name);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}