package io.metadew.iesi.server.rest.script;

import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.tools.IdentifierTools;
import io.metadew.iesi.server.rest.error.DataBadRequestException;
import io.metadew.iesi.server.rest.resource.HalMultipleEmbeddedResource;
import io.metadew.iesi.server.rest.script.dto.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@Tag(name = "scripts", description = "Everything about scripts")
@RequestMapping("/scripts")
@CrossOrigin
public class ScriptsController {

    private final IScriptService scriptService;
    private final IScriptDtoService scriptDtoService;
    private final ScriptDtoModelAssembler scriptDtoModelAssembler;
    private final IScriptPostDtoService scriptPostDtoService;
    private final PagedResourcesAssembler<ScriptDto> scriptDtoPagedResourcesAssembler;

    @Autowired
    ScriptsController(IScriptService scriptService,
                      ScriptDtoModelAssembler scriptDtoModelAssembler,
                      IScriptPostDtoService scriptPostDtoService,
                      IScriptDtoService scriptDtoService,
                      @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
                             PagedResourcesAssembler<ScriptDto> scriptDtoPagedResourcesAssembler) {
        this.scriptService = scriptService;
        this.scriptDtoService = scriptDtoService;
        this.scriptDtoModelAssembler = scriptDtoModelAssembler;
        this.scriptPostDtoService = scriptPostDtoService;
        this.scriptDtoPagedResourcesAssembler = scriptDtoPagedResourcesAssembler;
    }

    @GetMapping("")
    @PreAuthorize("hasPrivilege('SCRIPTS_READ')")
    public PagedModel<ScriptDto> getAll(Pageable pageable,
                                        @RequestParam(required = false, name = "expand", defaultValue = "") List<String> expansions,
                                        @RequestParam(required = false, name = "version") String version,
                                        @RequestParam(required = false, name = "name") String name,
                                        @RequestParam(required = false, name = "label") String labelKeyCombination) {
        List<ScriptFilter> scriptFilters = extractScriptFilterOptions(name, labelKeyCombination);
        boolean lastVersion = extractLastVersion(version);
        Page<ScriptDto> scriptDtoPage = scriptDtoService
                .getAll(pageable, expansions, lastVersion, scriptFilters);
        if (scriptDtoPage.hasContent())
            return scriptDtoPagedResourcesAssembler.toModel(scriptDtoPage, scriptDtoModelAssembler::toModel);
        //noinspection unchecked
        return (PagedModel<ScriptDto>) scriptDtoPagedResourcesAssembler.toEmptyModel(scriptDtoPage, ScriptDto.class);
    }

    private boolean extractLastVersion(String version) {
        return version != null && version.toLowerCase().equals("latest");
    }

    private List<ScriptFilter> extractScriptFilterOptions(String name, String labelKeyCombination) {
        List<ScriptFilter> scriptFilters = new ArrayList<>();
        if (name != null) {
            scriptFilters.add(new ScriptFilter(ScriptFilterOption.NAME, name, false));
        }
        if (labelKeyCombination != null) {
            scriptFilters.add(new ScriptFilter(ScriptFilterOption.LABEL, labelKeyCombination, false));
        }
        return scriptFilters;
    }

    @GetMapping("/{name}")
    @PreAuthorize("hasPrivilege('SCRIPTS_READ')")
    public PagedModel<ScriptDto> getByName(Pageable pageable,
                                           @PathVariable String name,
                                           @RequestParam(required = false, name = "expand", defaultValue = "") List<String> expansions,
                                           @RequestParam(required = false, name = "version") String version) {
        Page<ScriptDto> scriptDtoPage = scriptDtoService
                .getByName(pageable, name, expansions, version != null && version.toLowerCase().equals("latest"));
        if (scriptDtoPage.hasContent())
            return scriptDtoPagedResourcesAssembler.toModel(scriptDtoPage, scriptDtoModelAssembler::toModel);
        //noinspection unchecked - disable warning on casting
        return (PagedModel<ScriptDto>) scriptDtoPagedResourcesAssembler.toEmptyModel(scriptDtoPage, ScriptDto.class);
    }

    @GetMapping("/{name}/{version}")
    @PreAuthorize("hasPrivilege('SCRIPTS_READ')")
    public ScriptDto get(@PathVariable String name,
                         @PathVariable Long version,
                         @RequestParam(required = false, name = "expand", defaultValue = "") List<String> expansions) throws MetadataDoesNotExistException {
        ScriptDto scriptDto = scriptDtoService.getByNameAndVersion(name, version, expansions)
                .orElseThrow(() -> new MetadataDoesNotExistException(new ScriptKey(IdentifierTools.getScriptIdentifier(name), version)));
        return scriptDtoModelAssembler.toModel(scriptDto);
    }

    @PostMapping("")
    @PreAuthorize("hasPrivilege('SCRIPTS_WRITE')")
    public ScriptDto post(@Valid @RequestBody ScriptPostDto scriptPostDto) throws MetadataAlreadyExistsException {
        scriptService.createScript(scriptPostDto);
        return scriptDtoModelAssembler.toModel(scriptPostDtoService.convertToEntity(scriptPostDto));
    }


    @PutMapping("")
    @PreAuthorize("hasPrivilege('SCRIPTS_WRITE')")
    public HalMultipleEmbeddedResource<ScriptPostDto> putAll(@Valid @RequestBody List<ScriptPostDto> scriptDtos) throws MetadataDoesNotExistException {
        scriptService.updateScripts(scriptDtos);
        HalMultipleEmbeddedResource<ScriptPostDto> halMultipleEmbeddedResource = new HalMultipleEmbeddedResource<>();
        for (ScriptPostDto scriptPostDto : scriptDtos) {
            halMultipleEmbeddedResource.embedResource(scriptPostDto);
        }
        halMultipleEmbeddedResource.add(
                linkTo(methodOn(ScriptsController.class)
                        .getAll(PageRequest.of(0, 20), new ArrayList<>(), "", null, null))
                        .withRel("scripts"));
        return halMultipleEmbeddedResource;
    }

    @PutMapping("/{name}/{version}")
    @PreAuthorize("hasPrivilege('SCRIPTS_WRITE')")
    public ScriptDto put(@PathVariable String name,
                         @PathVariable long version,
                         @RequestBody ScriptPostDto scriptPostDto) throws MetadataDoesNotExistException {
        if (!scriptPostDto.getName().equals(name)) throw new DataBadRequestException(name);
        if (scriptPostDto.getVersion().getNumber() != version) throw new DataBadRequestException(version);
        scriptService.updateScript(scriptPostDto);
        return scriptDtoModelAssembler.toModel(scriptPostDtoService.convertToEntity(scriptPostDto));
    }

    @DeleteMapping("/{name}")
    @PreAuthorize("hasPrivilege('SCRIPTS_DELETE')")
    public ResponseEntity<?> deleteByName(@PathVariable String name) {
        scriptService.deleteByName(name);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{name}/{version}")
    @PreAuthorize("hasPrivilege('SCRIPTS_DELETE')")
    public ResponseEntity<?> delete(@PathVariable String name, @PathVariable long version) throws MetadataDoesNotExistException {
        scriptService.deleteByNameAndVersion(name, version);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}