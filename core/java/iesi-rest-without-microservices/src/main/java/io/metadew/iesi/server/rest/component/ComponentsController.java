package io.metadew.iesi.server.rest.component;

import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.component.key.ComponentKey;
import io.metadew.iesi.metadata.service.user.IESIPrivilege;
import io.metadew.iesi.metadata.tools.IdentifierTools;
import io.metadew.iesi.server.rest.component.dto.ComponentDto;
import io.metadew.iesi.server.rest.component.dto.ComponentDtoResourceAssembler;
import io.metadew.iesi.server.rest.component.dto.IComponentDtoService;
import io.metadew.iesi.server.rest.configuration.security.IesiSecurityChecker;
import io.metadew.iesi.server.rest.error.DataBadRequestException;
import io.metadew.iesi.server.rest.resource.HalMultipleEmbeddedResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/components")
@ConditionalOnWebApplication
public class ComponentsController {

    private final IComponentService componentService;
    private final IComponentDtoService componentDtoService;
    private final ComponentDtoResourceAssembler componentDtoResourceAssembler;
    private final PagedResourcesAssembler<ComponentDto> componentDtoPagedResourcesAssembler;
    private final IesiSecurityChecker iesiSecurityChecker;

    @Autowired
    ComponentsController(ComponentDtoResourceAssembler componentDtoResourceAssembler,
                         IComponentService componentService,
                         IComponentDtoService componentDtoService,
                         PagedResourcesAssembler<ComponentDto> componentDtoPagedResourcesAssembler,
                         IesiSecurityChecker iesiSecurityChecker
    ) {
        this.componentDtoResourceAssembler = componentDtoResourceAssembler;
        this.componentService = componentService;
        this.componentDtoService = componentDtoService;
        this.componentDtoPagedResourcesAssembler = componentDtoPagedResourcesAssembler;
        this.iesiSecurityChecker = iesiSecurityChecker;
    }

    @GetMapping("")
    @PreAuthorize("hasPrivilege('COMPONENTS_READ')")
    public PagedModel<ComponentDto> getAll(Pageable pageable, @RequestParam(required = false, name = "name") String name) {
        List<ComponentFilter> componentFilters = extractComponentFilterOptions(name);
        Page<ComponentDto> componentDtoPage = componentDtoService.getAll(SecurityContextHolder.getContext().getAuthentication(), pageable, componentFilters);

        if (componentDtoPage.hasContent())
            return componentDtoPagedResourcesAssembler.toModel(componentDtoPage, componentDtoResourceAssembler::toModel);

        return (PagedModel<ComponentDto>) componentDtoPagedResourcesAssembler.toEmptyModel(componentDtoPage, ComponentDto.class);
    }

    private List<ComponentFilter> extractComponentFilterOptions(String name) {
        List<ComponentFilter> componentFilters = new ArrayList<>();
        if (name != null) {
            componentFilters.add(new ComponentFilter(ComponentFilterOption.NAME, name, false));
        }
        return componentFilters;
    }

    @GetMapping("/{name}")
    @PreAuthorize("hasPrivilege('COMPONENTS_READ')")
    public PagedModel<ComponentDto> getByName(Pageable pageable, @PathVariable String name) {
        Page<ComponentDto> componentDtoPage = componentDtoService.getByName(SecurityContextHolder.getContext().getAuthentication(), pageable, name);
        if (componentDtoPage.hasContent())
            return componentDtoPagedResourcesAssembler.toModel(componentDtoPage, componentDtoResourceAssembler::toModel);

        return (PagedModel<ComponentDto>) componentDtoPagedResourcesAssembler.toEmptyModel(componentDtoPage, ComponentDto.class);
    }

    @GetMapping("/{name}/{version}")
    @PreAuthorize("hasPrivilege('COMPONENTS_READ')")
    @PostAuthorize("hasPrivilege('COMPONENTS_READ', returnObject.securityGroupName)")
    public ComponentDto get(@PathVariable String name, @PathVariable Long version) throws MetadataDoesNotExistException {
        return componentDtoService.getByNameAndVersion(null, name, version)
                .orElseThrow(() -> new MetadataDoesNotExistException(new ComponentKey(IdentifierTools.getComponentIdentifier(name), version)));
    }

    @PostMapping("")
    @PreAuthorize("hasPrivilege('COMPONENTS_WRITE', #componentDto.securityGroupName)")
    public ComponentDto post(@Valid @RequestBody ComponentDto componentDto) {
        try {
            componentService.createComponent(componentDto);
            return componentDtoResourceAssembler.toModel(componentDtoService.convertToEntity(componentDto));
        } catch (MetadataAlreadyExistsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Component " + componentDto.getName() + " already exists");
        }
    }

    @PutMapping("")
    @PreAuthorize("hasPrivilege('COMPONENTS_WRITE', #componentDtos.![securityGroupName])")
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
    @PreAuthorize("hasPrivilege('COMPONENTS_WRITE', #componentDto.securityGroupName)")
    public ComponentDto put(@PathVariable String name, @PathVariable Long version, @RequestBody ComponentDto componentDto) throws MetadataDoesNotExistException {
        if (!componentDto.getName().equals(name)) {
            throw new DataBadRequestException(name);
        } else if (componentDto.getVersion().getNumber() != version) {
            throw new DataBadRequestException(version);
        }
        componentService.updateComponent(componentDto);
        return componentDtoResourceAssembler.toModel(componentDtoService.convertToEntity(componentDto));

    }

    @DeleteMapping("/{name}/{version}")
    @PreAuthorize("hasPrivilege('COMPONENTS_WRITE')")
    public ResponseEntity<?> delete(@PathVariable String name, @PathVariable Long version) throws MetadataDoesNotExistException {
        ComponentDto componentDto = componentDtoService.getByNameAndVersion(null, name, version)
                .orElseThrow(() -> new MetadataDoesNotExistException(new ComponentKey(IdentifierTools.getComponentIdentifier(name), version)));
        if (!iesiSecurityChecker.hasPrivilege(SecurityContextHolder.getContext().getAuthentication(),
                IESIPrivilege.COMPONENTS_MODIFY.getPrivilege(),
                componentDto.getSecurityGroupName())
        ) {
            throw new AccessDeniedException("User is not allowed to delete this component");
        }
        componentService.deleteByNameAndVersion(name, version);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}