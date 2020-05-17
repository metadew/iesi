package io.metadew.iesi.server.rest.component;

import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.component.Component;
import io.metadew.iesi.metadata.definition.component.key.ComponentKey;
import io.metadew.iesi.metadata.tools.IdentifierTools;
import io.metadew.iesi.server.rest.component.dto.ComponentDto;
import io.metadew.iesi.server.rest.component.resource.ComponentDtoResourceAssembler;
import io.metadew.iesi.server.rest.error.DataBadRequestException;
import io.metadew.iesi.server.rest.resource.HalMultipleEmbeddedResource;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@Tag(name = "components", description = "Everything about components")
@RequestMapping("/components")
public class ComponentsController {

    private IComponentService componentService;
    private ComponentDtoResourceAssembler componentDtoResourceAssembler;

    @Autowired
    ComponentsController(ComponentDtoResourceAssembler componentDtoResourceAssembler,
                         ComponentService componentService) {
        this.componentDtoResourceAssembler = componentDtoResourceAssembler;
        this.componentService = componentService;
    }

    @GetMapping("")
    public HalMultipleEmbeddedResource<ComponentDto> getAll() {
        List<Component> components = componentService.getAll();
        return new HalMultipleEmbeddedResource<>(components.stream()
                .map(component -> componentDtoResourceAssembler.toModel(component))
                .collect(Collectors.toList()));
    }

    @GetMapping("/{name}")
    public HalMultipleEmbeddedResource<ComponentDto> getByName(@PathVariable String name) {
        List<Component> components = componentService.getByName(name);
        return new HalMultipleEmbeddedResource<>(components.stream()
                .map(component -> componentDtoResourceAssembler.toModel(component))
                .collect(Collectors.toList()));
    }

    @GetMapping("/{name}/{version}")
    public ComponentDto get(@PathVariable String name, @PathVariable Long version) throws MetadataDoesNotExistException {
        Component component = componentService.getByNameAndVersion(name, version).
                orElseThrow(() -> new MetadataDoesNotExistException(new ComponentKey(IdentifierTools.getComponentIdentifier(name), version)));
        return componentDtoResourceAssembler.toModel(component);
    }

    @PostMapping("")
    public ComponentDto post(@Valid @RequestBody ComponentDto component) {
        try {
            componentService.createComponent(component);
            return componentDtoResourceAssembler.toModel(component.convertToEntity());
        } catch (MetadataAlreadyExistsException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Component " + component.getName() + " already exists");
        }

    }

    @PutMapping("")
    public HalMultipleEmbeddedResource<ComponentDto> putAll(@Valid @RequestBody List<ComponentDto> componentDtos) throws MetadataDoesNotExistException {
        componentService.updateComponents(componentDtos);
        HalMultipleEmbeddedResource<ComponentDto> halMultipleEmbeddedResource = new HalMultipleEmbeddedResource<>();
        for (ComponentDto componentDto : componentDtos) {
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
        componentService.updateComponent(component);
        return componentDtoResourceAssembler.toModel(component.convertToEntity());

    }

    @DeleteMapping("")
    public ResponseEntity<?> deleteAll() {
        componentService.deleteAll();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<?> deleteByName(@PathVariable String name) {
        componentService.deleteByName(name);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{name}/{version}")
    public ResponseEntity<?> delete(@PathVariable String name, @PathVariable Long version) throws MetadataDoesNotExistException {
        componentService.deleteByNameAndVersion(name, version);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}