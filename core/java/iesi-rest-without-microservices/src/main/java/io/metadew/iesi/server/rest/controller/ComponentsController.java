package io.metadew.iesi.server.rest.controller;

import io.metadew.iesi.metadata.configuration.component.ComponentConfiguration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.component.Component;
import io.metadew.iesi.metadata.definition.component.key.ComponentKey;
import io.metadew.iesi.metadata.tools.IdentifierTools;
import io.metadew.iesi.server.rest.error.DataBadRequestException;
import io.metadew.iesi.server.rest.resource.HalMultipleEmbeddedResource;
import io.metadew.iesi.server.rest.resource.component.dto.ComponentByNameDto;
import io.metadew.iesi.server.rest.resource.component.dto.ComponentDto;
import io.metadew.iesi.server.rest.resource.component.dto.ComponentGlobalDto;
import io.metadew.iesi.server.rest.resource.component.resource.ComponentDtoResourceAssembler;
import io.metadew.iesi.server.rest.resource.component.resource.ComponentGetByNameDtoAssembler;
import io.metadew.iesi.server.rest.resource.component.resource.ComponentGlobalDtoResourceAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static io.metadew.iesi.server.rest.helper.Filter.distinctByKey;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/components")
public class ComponentsController {

    private ComponentConfiguration componentConfiguration;
    private ComponentGetByNameDtoAssembler componentGetByNameGetDtoAssembler;
    private ComponentGlobalDtoResourceAssembler componentGlobalDtoResourceAssembler;
    private ComponentDtoResourceAssembler componentDtoResourceAssembler;

    @Autowired
    ComponentsController(ComponentConfiguration componentConfiguration, ComponentGlobalDtoResourceAssembler componentGlobalDtoResourceAssembler,
                         ComponentGetByNameDtoAssembler componentGetByNameGetDtoAssembler,
                         ComponentDtoResourceAssembler componentDtoResourceAssembler) {
        this.componentConfiguration = componentConfiguration;
        this.componentDtoResourceAssembler = componentDtoResourceAssembler;
        this.componentGetByNameGetDtoAssembler = componentGetByNameGetDtoAssembler;
        this.componentGlobalDtoResourceAssembler = componentGlobalDtoResourceAssembler;
    }

    @GetMapping("")
    public HalMultipleEmbeddedResource<ComponentGlobalDto> getAll() {
        List<Component> components = componentConfiguration.getAll();
        return new HalMultipleEmbeddedResource<>(components.stream()
                .filter(distinctByKey(Component::getName))
                .map(component -> componentGlobalDtoResourceAssembler.toModel(Collections.singletonList(component)))
                .collect(Collectors.toList()));
    }

    @GetMapping("/{name}")
    public ComponentByNameDto getByName(@PathVariable String name) {
        List<Component> component = componentConfiguration.getByID(IdentifierTools.getComponentIdentifier(name));
        return componentGetByNameGetDtoAssembler.toModel(component);
    }

    @GetMapping("/{name}/{version}")
    public ComponentDto get(@PathVariable String name, @PathVariable Long version) throws MetadataDoesNotExistException {
        Component component = componentConfiguration.get(new ComponentKey(IdentifierTools.getComponentIdentifier(name), version)).
                orElseThrow(() -> new MetadataDoesNotExistException(new ComponentKey(IdentifierTools.getComponentIdentifier(name), version)));
        return componentDtoResourceAssembler.toModel(component);
    }

    @PostMapping("/")
    public ComponentDto post(@Valid @RequestBody ComponentDto component) {
        try {
            componentConfiguration.insert(component.convertToEntity());
            return componentDtoResourceAssembler.toModel(component.convertToEntity());
        } catch (MetadataAlreadyExistsException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Component " + component.getName() + " already exists");
        }

    }

    @PutMapping("/")
    public HalMultipleEmbeddedResource<ComponentDto> putAll(@Valid @RequestBody List<ComponentDto> componentDtos) throws MetadataDoesNotExistException {
        HalMultipleEmbeddedResource<ComponentDto> halMultipleEmbeddedResource = new HalMultipleEmbeddedResource<>();
        for (ComponentDto componentDto : componentDtos) {
            componentConfiguration.update(componentDto.convertToEntity());
            halMultipleEmbeddedResource.embedResource(componentDto);
            halMultipleEmbeddedResource.add(linkTo(methodOn(ComponentsController.class)
                    .get(componentDto.getName(), componentDto.getVersion().getNumber()))
                    .withRel(componentDto.getName() + ":" + componentDto.getVersion().getNumber()));
        }

        return halMultipleEmbeddedResource;
    }

    @PutMapping("/{name}/{version}")
    public ComponentDto put(@PathVariable String name, @PathVariable Long version, @RequestBody ComponentDto component) throws MetadataDoesNotExistException {
        if (!component.getName().equals(name)) {
            throw new DataBadRequestException(name);
        } else if (component.getVersion().getNumber() != version) {
            throw new DataBadRequestException(version);
        }
        componentConfiguration.update(component.convertToEntity());
        return componentDtoResourceAssembler.toModel(component.convertToEntity());

    }

    @DeleteMapping("/")
    public ResponseEntity<?> deleteAll() {
        componentConfiguration.deleteAll();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<?> deleteByName(@PathVariable String name) {
        componentConfiguration.deleteById(IdentifierTools.getComponentIdentifier(name));
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{name}/{version}")
    public ResponseEntity<?> delete(@PathVariable String name, @PathVariable Long version) throws MetadataDoesNotExistException {
        Component component = componentConfiguration.get(new ComponentKey(IdentifierTools.getComponentIdentifier(name), version))
                .orElseThrow(() -> new MetadataDoesNotExistException(new ComponentKey(IdentifierTools.getComponentIdentifier(name), version)));
        componentConfiguration.delete(component.getMetadataKey());
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}