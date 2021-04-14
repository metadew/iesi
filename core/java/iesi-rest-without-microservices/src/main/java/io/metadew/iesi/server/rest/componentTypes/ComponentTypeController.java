package io.metadew.iesi.server.rest.componentTypes;

import io.metadew.iesi.common.configuration.metadata.componenttypes.MetadataComponentTypesConfiguration;
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
    private IComponentTypeDtoService componentTypeDtoService;

    @Autowired
    ComponentTypeController(IComponentTypeDtoService componentTypeDtoService) {
        this.componentTypeDtoService = componentTypeDtoService;
    }

    @GetMapping("")
    public List<ComponentTypeDto> getAll() {
        return MetadataComponentTypesConfiguration.getInstance().getComponentTypes()
                .entrySet()
                .stream()
                .map(entry -> componentTypeDtoService.convertToDto(entry.getValue(), entry.getKey()))
                .collect(Collectors.toList());
    }

    @GetMapping("/{name}")
    public ComponentTypeDto getByName(@PathVariable String name) {
        return MetadataComponentTypesConfiguration.getInstance().getComponentType(name)
                .map(actionType -> componentTypeDtoService.convertToDto(actionType, name))
                .orElseThrow(() -> new RuntimeException("Could not find action type " + name));
    }
}
