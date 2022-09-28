package io.metadew.iesi.server.rest.componentTypes;

import io.metadew.iesi.common.configuration.metadata.componenttypes.MetadataComponentTypesConfiguration;
import io.metadew.iesi.server.rest.componentTypes.dto.ComponentTypeDto;
import io.metadew.iesi.server.rest.componentTypes.dto.ComponentTypeDtoResourceAssembler;
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
@RequestMapping("/component-types")
@ConditionalOnWebApplication
public class ComponentTypeController {

    private final ComponentTypeDtoResourceAssembler componentTypeDtoResourceAssembler;
    private final MetadataComponentTypesConfiguration metadataComponentTypesConfiguration;

    @Autowired
    ComponentTypeController(ComponentTypeDtoResourceAssembler componentTypeDtoResourceAssembler, MetadataComponentTypesConfiguration metadataComponentTypesConfiguration) {
        this.componentTypeDtoResourceAssembler = componentTypeDtoResourceAssembler;
        this.metadataComponentTypesConfiguration = metadataComponentTypesConfiguration;
    }

    @GetMapping("")
    public List<ComponentTypeDto> getAll() {
        return metadataComponentTypesConfiguration.getComponentTypes()
                .entrySet()
                .stream()
                .map(entry -> {
                    entry.getValue().setName(entry.getKey());
                    return componentTypeDtoResourceAssembler.toModel(entry.getValue());
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/{name}")
    public ComponentTypeDto getByName(@PathVariable String name) {
        return metadataComponentTypesConfiguration.getComponentType(name)
                .map(componentType -> componentTypeDtoResourceAssembler.toModel(componentType))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find action type " + name));
    }
}
