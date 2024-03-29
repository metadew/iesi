package io.metadew.iesi.server.rest.connectiontypes;

import io.metadew.iesi.common.configuration.metadata.connectiontypes.MetadataConnectionTypesConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/connection-types")
@ConditionalOnWebApplication
public class ConnectionTypeController {

    private final IConnectionTypeDtoService connectionTypeDtoService;
    private final MetadataConnectionTypesConfiguration metadataConnectionTypesConfiguration;

    @Autowired
    ConnectionTypeController(IConnectionTypeDtoService connectionTypeDtoService, MetadataConnectionTypesConfiguration metadataConnectionTypesConfiguration) {
        this.connectionTypeDtoService = connectionTypeDtoService;
        this.metadataConnectionTypesConfiguration = metadataConnectionTypesConfiguration;
    }

    @GetMapping("")
    public List<ConnectionTypeDto> getAll() {
        return metadataConnectionTypesConfiguration.getConnectionTypes()
                .entrySet()
                .stream()
                .map(entry -> connectionTypeDtoService.convertToDto(entry.getValue(), entry.getKey()))
                .collect(Collectors.toList());
    }

    @GetMapping("/{name}")
    public ConnectionTypeDto getByName(@PathVariable String name) {
        return metadataConnectionTypesConfiguration.getConnectionType(name)
                .map(actionType -> connectionTypeDtoService.convertToDto(actionType, name))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find action type " + name));
    }

}