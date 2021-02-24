package io.metadew.iesi.server.rest.connectiontypes;

import io.metadew.iesi.common.configuration.metadata.connectiontypes.MetadataConnectionTypesConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/connection-types")
public class ConnectionTypeController {

    private IConnectionTypeDtoService connectionTypeDtoService;

    @Autowired
    ConnectionTypeController(IConnectionTypeDtoService connectionTypeDtoService) {
        this.connectionTypeDtoService = connectionTypeDtoService;
    }

    @GetMapping("")
    public List<ConnectionTypeDto> getAll() {
        return MetadataConnectionTypesConfiguration.getInstance().getConnectionTypes()
                .entrySet()
                .stream()
                .map(entry -> connectionTypeDtoService.convertToDto(entry.getValue(), entry.getKey()))
                .collect(Collectors.toList());
    }

    @GetMapping("/{name}")
    public ConnectionTypeDto getByName(@PathVariable String name) {
        return MetadataConnectionTypesConfiguration.getInstance().getConnectionType(name)
                .map(actionType -> connectionTypeDtoService.convertToDto(actionType, name))
                .orElseThrow(() -> new RuntimeException("Could not find action type " + name));
    }

}