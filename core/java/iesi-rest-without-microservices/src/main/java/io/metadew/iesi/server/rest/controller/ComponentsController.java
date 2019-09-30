package io.metadew.iesi.server.rest.controller;

import io.metadew.iesi.metadata.configuration.component.ComponentConfiguration;
import io.metadew.iesi.metadata.configuration.exception.ComponentAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.ComponentDoesNotExistException;
import io.metadew.iesi.metadata.definition.component.Component;
import io.metadew.iesi.server.rest.error.DataBadRequestException;
import io.metadew.iesi.server.rest.error.DataNotFoundException;
import io.metadew.iesi.server.rest.error.GetListNullProperties;
import io.metadew.iesi.server.rest.error.GetNullProperties;
import io.metadew.iesi.server.rest.pagination.ComponentCriteria;
import io.metadew.iesi.server.rest.pagination.ComponentPagination;
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
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static io.metadew.iesi.server.rest.helper.Filter.distinctByKey;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping("/components")
public class ComponentsController {

    private ComponentConfiguration componentConfiguration;
    private final ComponentPagination componentPagination;
    private ComponentGetByNameDtoAssembler componentGetByNameGetDtoAssembler;
    private ComponentGlobalDtoResourceAssembler componentGlobalDtoResourceAssembler;
    private ComponentDtoResourceAssembler componentDtoResourceAssembler;

    @Autowired
    ComponentsController(ComponentConfiguration componentConfiguration, ComponentGlobalDtoResourceAssembler componentGlobalDtoResourceAssembler,
                         ComponentPagination componentPagination, ComponentGetByNameDtoAssembler componentGetByNameGetDtoAssembler,
                         ComponentDtoResourceAssembler componentDtoResourceAssembler) {
        this.componentConfiguration = componentConfiguration;
        this.componentPagination = componentPagination;
        this.componentDtoResourceAssembler = componentDtoResourceAssembler;
        this.componentGetByNameGetDtoAssembler = componentGetByNameGetDtoAssembler;
        this.componentGlobalDtoResourceAssembler = componentGlobalDtoResourceAssembler;
    }


    @GetMapping("")
    public HalMultipleEmbeddedResource<ComponentGlobalDto> getAll(@Valid ComponentCriteria componentCriteria) {
        List<Component> components = componentConfiguration.getAll();
        List<Component> pagination = componentPagination.search(components, componentCriteria);
        return new HalMultipleEmbeddedResource<>(pagination.stream()
                .filter(distinctByKey(Component :: getName))
                .map(component -> componentGlobalDtoResourceAssembler.toResource(Collections.singletonList(component)))
                .collect(Collectors.toList()));
    }


    @GetMapping("/{name}")
    public ComponentByNameDto getByName(@PathVariable String name) {
        List<Component> component = componentConfiguration.getByName(name);
        if (component.isEmpty()) {
            throw new DataNotFoundException(name);
        }
        return componentGetByNameGetDtoAssembler.toResource(component);
    }

    @GetMapping("/{name}/{version}")
    public ComponentDto get(@PathVariable String name, @PathVariable Long version) {
        Component component = componentConfiguration.get(name, version).
                orElseThrow(() -> new DataNotFoundException(name, version));
        return componentDtoResourceAssembler.toResource(component);
    }

    @PostMapping("/")
    public ComponentDto post(@Valid @RequestBody ComponentDto component) {
        try {
            componentConfiguration.insert(component.convertToEntity());
            return componentDtoResourceAssembler.toResource(component.convertToEntity());
        } catch (ComponentAlreadyExistsException | SQLException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Component " + component.getName() + " already exists");
        }

    }


    @PutMapping("/")
    public HalMultipleEmbeddedResource<ComponentDto> putAll(@Valid @RequestBody List<ComponentDto> componentDtos) {
        HalMultipleEmbeddedResource<ComponentDto> halMultipleEmbeddedResource = new HalMultipleEmbeddedResource<>();
        for (ComponentDto componentDto : componentDtos) {
            try {
                componentConfiguration.update(componentDto.convertToEntity());
                halMultipleEmbeddedResource.embedResource(componentDto);
                halMultipleEmbeddedResource.add(linkTo(methodOn(ComponentsController.class)
                        .get(componentDto.getName(), componentDto.getVersion().getNumber()))
                        .withRel(componentDto.getName() + ":" + componentDto.getVersion().getNumber()));
            } catch (ComponentDoesNotExistException | SQLException e) {
                e.printStackTrace();
                throw new DataNotFoundException(componentDto.getName());
            }
        }

        return halMultipleEmbeddedResource;
    }

    @PutMapping("/{name}/{version}")
    public ComponentDto put(@PathVariable String name, @PathVariable Long version, @RequestBody ComponentDto component) {
        if (!component.getName().equals(name)) {
            throw new DataBadRequestException(name);
        } else if (!componentConfiguration.get(name, version).isPresent()) {
            throw new DataNotFoundException(name);
        }
        try {
            componentConfiguration.update(component.convertToEntity());
            return componentDtoResourceAssembler.toResource(component.convertToEntity());
        } catch (ComponentDoesNotExistException | SQLException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }

    }

    @DeleteMapping("/")
    public ResponseEntity<?> deleteAll() {
        componentConfiguration.deleteAll();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<?> deleteByName(@PathVariable String name) {
        List<Component> components = componentConfiguration.getByName(name);
        if (components.isEmpty()) {
            throw new DataNotFoundException(name);
        }
        try {
            componentConfiguration.deleteByName(name);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (ComponentDoesNotExistException | SQLException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @DeleteMapping("/{name}/{version}")
    public ResponseEntity<?> deleteC(@PathVariable String name, @PathVariable Long version) {
        Component component = componentConfiguration.get(name, version)
                .orElseThrow(() -> new DataNotFoundException(name, version));
        try {
            componentConfiguration.delete(component);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (ComponentDoesNotExistException | SQLException e) {
            e.printStackTrace();
            throw new DataNotFoundException(name, version);

        }
    }
}