package io.metadew.iesi.server.rest.component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.component.Component;
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
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
    private final ObjectMapper objectMapper;

    @Autowired
    ComponentsController(ComponentDtoResourceAssembler componentDtoResourceAssembler,
                         IComponentService componentService,
                         IComponentDtoService componentDtoService,
                         PagedResourcesAssembler<ComponentDto> componentDtoPagedResourcesAssembler,
                         IesiSecurityChecker iesiSecurityChecker,
                         ObjectMapper objectMapper
    ) {
        this.componentDtoResourceAssembler = componentDtoResourceAssembler;
        this.componentService = componentService;
        this.componentDtoService = componentDtoService;
        this.componentDtoPagedResourcesAssembler = componentDtoPagedResourcesAssembler;
        this.iesiSecurityChecker = iesiSecurityChecker;
        this.objectMapper = objectMapper;
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

    @GetMapping("/{name}/{version}/download")
    @PreAuthorize("hasPrivilege('COMPONENTS_READ')")
    public ResponseEntity<Resource> getFile(@PathVariable String name, @PathVariable long version) {
        Component component = componentService.getByNameAndVersion(name, version)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("The component %s with version %s does not exist", name, version)));

        ContentDisposition contentDisposition = ContentDisposition.builder("inline")
                .filename(String.format("component_%s_%s.json", name, version))
                .build();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentDisposition(contentDisposition);

        try {
            String jsonString = objectMapper.writeValueAsString(new Component(
                    null,
                    component.getSecurityGroupKey(),
                    component.getSecurityGroupName(),
                    component.getType(),
                    component.getName(),
                    component.getDescription(),
                    component.getVersion(),
                    component.getParameters(),
                    component.getAttributes()
            ));
            byte[] data = jsonString.getBytes(StandardCharsets.UTF_8);
            ByteArrayResource resource = new ByteArrayResource(data);

            return ResponseEntity.ok().headers(httpHeaders).contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
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

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<ComponentDto>> importComponents(@RequestParam(value = "file") MultipartFile multipartFile) {
        try {
            String textPlain = new String(multipartFile.getBytes());
            List<Component> components = componentService.importComponents(textPlain);
            return ResponseEntity.ok(componentDtoResourceAssembler.toModel(components));
        } catch (IOException e) {
            throw new RuntimeException(String.format("Cannot process the given file: %s", multipartFile.getOriginalFilename()));
        }
    }

    @PostMapping(value = "/import", consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<List<ComponentDto>> importComponents(@RequestBody String textPlain) {
        List<Component> components = componentService.importComponents(textPlain);
        return ResponseEntity.ok(componentDtoResourceAssembler.toModel(components));
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