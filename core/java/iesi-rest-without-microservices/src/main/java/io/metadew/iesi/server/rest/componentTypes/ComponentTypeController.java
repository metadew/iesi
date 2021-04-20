package io.metadew.iesi.server.rest.componentTypes;

import io.metadew.iesi.common.configuration.metadata.componenttypes.MetadataComponentTypesConfiguration;
import io.metadew.iesi.server.rest.componentTypes.dto.ComponentTypeDto;
import io.metadew.iesi.server.rest.componentTypes.dto.ComponentTypeDtoResourceAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/component-types")
public class ComponentTypeController {
    private ComponentTypeDtoResourceAssembler componentTypeDtoResourceAssembler;

    @Autowired
    ComponentTypeController(ComponentTypeDtoResourceAssembler componentTypeDtoResourceAssembler) {
        this.componentTypeDtoResourceAssembler = componentTypeDtoResourceAssembler;
    }

    @GetMapping("")
    public List<ComponentTypeDto> getAll() {
        return MetadataComponentTypesConfiguration.getInstance().getComponentTypes()
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
        return MetadataComponentTypesConfiguration.getInstance().getComponentType(name)
                .map(componentType -> componentTypeDtoResourceAssembler.toModel(componentType))
                .orElseThrow(() -> new RuntimeException("Could not find action type " + name));
    }
}
