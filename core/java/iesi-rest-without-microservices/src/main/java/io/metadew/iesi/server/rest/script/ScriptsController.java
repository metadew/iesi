package io.metadew.iesi.server.rest.script;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.service.user.IESIPrivilege;
import io.metadew.iesi.metadata.tools.IdentifierTools;
import io.metadew.iesi.server.rest.configuration.security.IesiSecurityChecker;
import io.metadew.iesi.server.rest.error.DataBadRequestException;
import io.metadew.iesi.server.rest.resource.HalMultipleEmbeddedResource;
import io.metadew.iesi.server.rest.script.dto.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@Tag(name = "scripts", description = "Everything about scripts")
@RequestMapping("/scripts")
public class ScriptsController {

    private final IScriptService scriptService;
    private final IScriptDtoService scriptDtoService;
    private final ScriptDtoModelAssembler scriptDtoModelAssembler;
    private final IScriptPostDtoService scriptPostDtoService;
    private final PagedResourcesAssembler<ScriptDto> scriptDtoPagedResourcesAssembler;
    private final IesiSecurityChecker iesiSecurityChecker;
    private final ObjectMapper objectMapper;

    @Autowired
    ScriptsController(IScriptService scriptService,
                      ScriptDtoModelAssembler scriptDtoModelAssembler,
                      IScriptPostDtoService scriptPostDtoService,
                      IScriptDtoService scriptDtoService,
                      PagedResourcesAssembler<ScriptDto> scriptDtoPagedResourcesAssembler,
                      IesiSecurityChecker iesiSecurityChecker,
                      ObjectMapper objectMapper) {
        this.scriptService = scriptService;
        this.scriptDtoService = scriptDtoService;
        this.scriptDtoModelAssembler = scriptDtoModelAssembler;
        this.scriptPostDtoService = scriptPostDtoService;
        this.scriptDtoPagedResourcesAssembler = scriptDtoPagedResourcesAssembler;
        this.iesiSecurityChecker = iesiSecurityChecker;
        this.objectMapper = objectMapper;
    }

    @GetMapping("")
    @PreAuthorize("hasPrivilege('SCRIPTS_READ')")
    public PagedModel<ScriptDto>  getAll(Pageable pageable,
                                        @RequestParam(required = false, name = "expand", defaultValue = "") List<String> expansions,
                                        @RequestParam(required = false, name = "version") String version,
                                        @RequestParam(required = false, name = "name") String name,
                                        @RequestParam(required = false, name = "label") String labelKeyCombination,
                                        @RequestParam(required = false, name = "includeInActive") String includeInActive ) {
        List<ScriptFilter> scriptFilters = extractScriptFilterOptions(name, labelKeyCombination, includeInActive);
        boolean lastVersion = extractLastVersion(version);
        Page<ScriptDto> scriptDtoPage = scriptDtoService
                .getAllActive(SecurityContextHolder.getContext().getAuthentication(),
                        pageable,
                        expansions,
                        lastVersion,
                        scriptFilters);
        if (scriptDtoPage.hasContent())
            return scriptDtoPagedResourcesAssembler.toModel(scriptDtoPage, scriptDtoModelAssembler::toModel);
        //noinspection unchecked
        return (PagedModel<ScriptDto>) scriptDtoPagedResourcesAssembler.toEmptyModel(scriptDtoPage, ScriptDto.class);
    }

    private boolean extractLastVersion(String version) {
        return version != null && version.equalsIgnoreCase("latest");
    }

    private List<ScriptFilter> extractScriptFilterOptions(String name, String labelKeyCombination, String includeInActive) {

        List<ScriptFilter> scriptFilters = new ArrayList<>();
        if (name != null) {
            scriptFilters.add(new ScriptFilter(ScriptFilterOption.NAME, name, false));
        }
        if (labelKeyCombination != null) {
            scriptFilters.add(new ScriptFilter(ScriptFilterOption.LABEL, labelKeyCombination, false));
        }

        if (includeInActive == null || !includeInActive.equalsIgnoreCase("true")) {
            includeInActive = "false";
        }
        scriptFilters.add(new ScriptFilter(ScriptFilterOption.INCLUDE_INACTIVE, includeInActive, false));
        return scriptFilters;
    }

    @GetMapping("/{name}")
    @PreAuthorize("hasPrivilege('SCRIPTS_READ')")
    public PagedModel<ScriptDto> getByName(Pageable pageable,
                                           @PathVariable String name,
                                           @RequestParam(required = false, name = "expand", defaultValue = "") List<String> expansions,
                                           @RequestParam(required = false, name = "version") String version) {
        Page<ScriptDto> scriptDtoPage = scriptDtoService
                .getByName(
                        null,
                        pageable,
                        name,
                        expansions,
                        version != null && version.equalsIgnoreCase("latest"));
        if (!iesiSecurityChecker.hasPrivilege(SecurityContextHolder.getContext().getAuthentication(),
                IESIPrivilege.SCRIPTS_READ.getPrivilege(),
                scriptDtoPage.get()
                        .map(ScriptDto::getSecurityGroupName)
                        .collect(Collectors.toList()))
        ) {
            throw new AccessDeniedException("User is not allowed to view this script");
        }
        if (scriptDtoPage.hasContent())
            return scriptDtoPagedResourcesAssembler.toModel(scriptDtoPage, scriptDtoModelAssembler::toModel);
        //noinspection unchecked - disable warning on casting
        return (PagedModel<ScriptDto>) scriptDtoPagedResourcesAssembler.toEmptyModel(scriptDtoPage, ScriptDto.class);

    }

    @GetMapping("/{name}/{version}")
    @PreAuthorize("hasPrivilege('SCRIPTS_READ')")
    @PostAuthorize("hasPrivilege('SCRIPTS_READ', returnObject.securityGroupName)")
    public ScriptDto get(@PathVariable String name,
                         @PathVariable Long version,
                         @RequestParam(required = false, name = "expand", defaultValue = "") List<String> expansions) {
        return scriptDtoService
                .getByNameAndVersion(
                        null,
                        name,
                        version,
                        expansions)
                .orElseThrow(() -> new MetadataDoesNotExistException(new ScriptKey(IdentifierTools.getScriptIdentifier(name), version)));
    }

    @GetMapping("/{name}/{version}/download")
    @PreAuthorize("hasPrivilege('SCRIPTS_READ')")
    public ResponseEntity<Resource> getFile(@PathVariable String name,
                                            @PathVariable Long version) throws IOException {

        ScriptDto scriptDto = scriptDtoService.getByNameAndVersion(null, name, version, new ArrayList<>())
                .orElseThrow(() -> new MetadataDoesNotExistException(new ScriptKey(IdentifierTools.getScriptIdentifier(name), version)));

        if (!iesiSecurityChecker.hasPrivilege(SecurityContextHolder.getContext().getAuthentication(),
                IESIPrivilege.SCRIPTS_READ.getPrivilege(),
                scriptDto.getSecurityGroupName())
        ) {
            throw new AccessDeniedException("User is not allowed to view this script");
        }

        ContentDisposition contentDisposition = ContentDisposition.builder("inline")
                .filename(String.format("script_%s_%d.json", name, version))
                .build();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentDisposition(contentDisposition);

        String jsonString = objectMapper.writeValueAsString(scriptDto);

        byte[] data = jsonString.getBytes();
        ByteArrayResource resource = new ByteArrayResource(data);

        return ResponseEntity.ok().headers(httpHeaders).contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource);

    }

    @PostMapping("")
    @PreAuthorize("hasPrivilege('SCRIPTS_WRITE', #scriptPostDto.securityGroupName)")
    public ScriptDto post(@Valid @RequestBody ScriptPostDto scriptPostDto) {
        scriptService.createScript(scriptPostDto);
        return scriptDtoModelAssembler.toModel(scriptPostDtoService.convertToEntity(scriptPostDto));
    }

    @PutMapping("")
    @PreAuthorize("hasPrivilege('SCRIPTS_WRITE', #scriptDtos.![securityGroupName])")
    public HalMultipleEmbeddedResource<ScriptPostDto> putAll(@Valid @RequestBody List<ScriptPostDto> scriptDtos) {
        scriptService.updateScripts(scriptDtos);
        HalMultipleEmbeddedResource<ScriptPostDto> halMultipleEmbeddedResource = new HalMultipleEmbeddedResource<>();
        for (ScriptPostDto scriptPostDto : scriptDtos) {
            halMultipleEmbeddedResource.embedResource(scriptPostDto);
        }
        halMultipleEmbeddedResource.add(
                linkTo(methodOn(ScriptsController.class)
                        .getAll(PageRequest.of(0, 20), new ArrayList<>(), "", null, null, null))
                        .withRel("scripts"));
        return halMultipleEmbeddedResource;
    }

    @PutMapping("/{name}/{version}")
    @PreAuthorize("hasPrivilege('SCRIPTS_WRITE', #scriptPostDto.securityGroupName)")
    public ScriptDto put(@PathVariable String name,
                         @PathVariable long version,
                         @RequestBody ScriptPostDto scriptPostDto) {
        if (!scriptPostDto.getName().equals(name)) throw new DataBadRequestException(name);
        if (scriptPostDto.getVersion().getNumber() != version) throw new DataBadRequestException(version);
        scriptService.updateScript(scriptPostDto);
        return scriptDtoModelAssembler.toModel(scriptPostDtoService.convertToEntity(scriptPostDto));
    }

    @DeleteMapping("/{name}/{version}")
    @PreAuthorize("hasPrivilege('SCRIPTS_WRITE')")
    public ResponseEntity<?> delete(@PathVariable String name, @PathVariable long version) {
        ScriptDto scriptDto = scriptDtoService
                .getByNameAndVersion(
                        null,
                        name,
                        version,
                        new ArrayList<>())
                .orElseThrow(() -> new MetadataDoesNotExistException(new ScriptKey(IdentifierTools.getScriptIdentifier(name), version)));

        if (!iesiSecurityChecker.hasPrivilege(SecurityContextHolder.getContext().getAuthentication(),
                IESIPrivilege.SCRIPTS_MODIFY.getPrivilege(),
                scriptDto.getSecurityGroupName())
        ) {
            throw new AccessDeniedException("User is not allowed to delete this script");
        }
        scriptService.deleteByNameAndVersion(name, version);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}