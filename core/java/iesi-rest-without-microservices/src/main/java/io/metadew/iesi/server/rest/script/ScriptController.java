package io.metadew.iesi.server.rest.script;

import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.tools.IdentifierTools;
import io.metadew.iesi.server.rest.error.DataBadRequestException;
import io.metadew.iesi.server.rest.resource.HalMultipleEmbeddedResource;
import io.metadew.iesi.server.rest.script.dto.IScriptPostDtoService;
import io.metadew.iesi.server.rest.script.dto.ScriptDto;
import io.metadew.iesi.server.rest.script.dto.ScriptPostDto;
import io.metadew.iesi.server.rest.script.dto.expansions.IScriptDtoExpansionService;
import io.metadew.iesi.server.rest.script.resource.ScriptDtoResourceAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Tag(name = "scripts", description = "Everything about scripts")
@RequestMapping("/scripts")
public class ScriptController {

    private IScriptService scriptService;
    private ScriptDtoResourceAssembler scriptDtoResourceAssembler;
    private IScriptPostDtoService scriptPostDtoService;
    private IScriptDtoExpansionService scriptDtoExpansionService;

    @Autowired
    ScriptController(IScriptService scriptService, ScriptDtoResourceAssembler scriptDtoResourceAssembler,
                     IScriptPostDtoService scriptPostDtoService, IScriptDtoExpansionService scriptDtoExpansionService) {
        this.scriptService = scriptService;
        this.scriptDtoResourceAssembler = scriptDtoResourceAssembler;
        this.scriptPostDtoService = scriptPostDtoService;
        this.scriptDtoExpansionService = scriptDtoExpansionService;
    }

    @GetMapping("")
    public HalMultipleEmbeddedResource<ScriptDto> getAll(@RequestParam(required = false, name = "expand") List<String> expansions) {
        List<Script> scripts = scriptService.getAll();
        return new HalMultipleEmbeddedResource<>(
                scripts.stream()
                        .map(script -> {
                            ScriptDto scriptDto = scriptDtoResourceAssembler.toModel(script);
                            scriptDtoExpansionService.addExpansions(scriptDto, expansions);
                            return scriptDto;
                        })
                        .collect(Collectors.toList()));
    }

    @GetMapping("/{name}")
    public HalMultipleEmbeddedResource<ScriptDto> getByName(@PathVariable String name, @RequestParam(required = false, name = "expand") List<String> expansions) {
        List<Script> scripts = scriptService.getByName(name);
        return new HalMultipleEmbeddedResource<>(scripts.stream()
                .map(script -> {
                    ScriptDto scriptDto = scriptDtoResourceAssembler.toModel(script);
                    scriptDtoExpansionService.addExpansions(scriptDto, expansions);
                    return scriptDto;
                })
                .collect(Collectors.toList()));
    }

    @GetMapping("/{name}/{version}")
    public ScriptDto get(@PathVariable String name, @PathVariable Long version, @RequestParam(required = false, name = "expand") List<String> expansions) throws MetadataDoesNotExistException {
        return scriptService.getByNameAndVersion(name, version)
                .map(script -> {
                    ScriptDto scriptDto = scriptDtoResourceAssembler.toModel(script);
                    scriptDtoExpansionService.addExpansions(scriptDto, expansions);
                    return scriptDto;
                })
                .orElseThrow(() -> new MetadataDoesNotExistException(new ScriptKey(IdentifierTools.getScriptIdentifier(name), version)));
    }

    @PostMapping("")
    public ScriptDto post(@Valid @RequestBody ScriptPostDto scriptPostDto) throws MetadataAlreadyExistsException {
        scriptService.createScript(scriptPostDto);
        return scriptDtoResourceAssembler.toModel(scriptPostDtoService.convertToEntity(scriptPostDto));
    }


    @PutMapping("")
    public HalMultipleEmbeddedResource<ScriptPostDto> putAll(@Valid @RequestBody List<ScriptPostDto> scriptDtos) throws MetadataDoesNotExistException {
        scriptService.updateScripts(scriptDtos);
        HalMultipleEmbeddedResource<ScriptPostDto> halMultipleEmbeddedResource = new HalMultipleEmbeddedResource<>();
        for (ScriptPostDto scriptPostDto : scriptDtos) {
            halMultipleEmbeddedResource.embedResource(scriptPostDto);
        }

        return halMultipleEmbeddedResource;
    }

    @PutMapping("/{name}/{version}")
    public ScriptDto put(@PathVariable String name, @PathVariable Long version,
                         @RequestBody ScriptPostDto scriptPostDto) throws MetadataDoesNotExistException {
        if (!scriptPostDto.getName().equals(name)) {
            throw new DataBadRequestException(name);
        }
        scriptService.updateScript(scriptPostDto);
        return scriptDtoResourceAssembler.toModel(scriptPostDtoService.convertToEntity(scriptPostDto));

    }

    @DeleteMapping("/{name}")
    public ResponseEntity<?> deleteByName(@PathVariable String name) {
        scriptService.deleteByName(name);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{name}/{version}")
    public ResponseEntity<?> delete(@PathVariable String name, @PathVariable Long version) throws MetadataDoesNotExistException {
        scriptService.deleteByNameAndVersion(name, version);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}