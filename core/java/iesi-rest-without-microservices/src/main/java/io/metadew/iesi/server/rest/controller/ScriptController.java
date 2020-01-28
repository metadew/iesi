package io.metadew.iesi.server.rest.controller;

import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.configuration.execution.ExecutionRequestConfiguration;
import io.metadew.iesi.metadata.configuration.script.ScriptConfiguration;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequestBuilder;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequestBuilderException;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequestBuilder;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequestBuilderException;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.metadata.definition.script.ScriptParameter;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.tools.IdentifierTools;
import io.metadew.iesi.server.rest.error.DataBadRequestException;
import io.metadew.iesi.server.rest.resource.HalMultipleEmbeddedResource;
import io.metadew.iesi.server.rest.resource.script.dto.ScriptByNameDto;
import io.metadew.iesi.server.rest.resource.script.dto.ScriptDto;
import io.metadew.iesi.server.rest.resource.script.dto.ScriptExecutionDto;
import io.metadew.iesi.server.rest.resource.script.dto.ScriptGlobalDto;
import io.metadew.iesi.server.rest.resource.script.resource.ScriptByNameDtoAssembler;
import io.metadew.iesi.server.rest.resource.script.resource.ScriptDtoResourceAssembler;
import io.metadew.iesi.server.rest.resource.script.resource.ScriptGlobalDtoResourceAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static io.metadew.iesi.server.rest.helper.Filter.distinctByKey;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping("/scripts")
public class ScriptController {

    private ExecutionRequestConfiguration executionRequestConfiguration;
    private ScriptConfiguration scriptConfiguration;
    private ScriptByNameDtoAssembler scriptByNameGetDtoAssembler;
    private ScriptDtoResourceAssembler scriptDtoResourceAssembler;
    private ScriptGlobalDtoResourceAssembler scriptGlobalDtoResourceAssembler;

    @Autowired
    ScriptController(ScriptConfiguration scriptConfiguration, ScriptDtoResourceAssembler scriptDtoResourceAssembler,
                     ScriptGlobalDtoResourceAssembler scriptGlobalDtoResourceAssembler, ScriptByNameDtoAssembler scriptByNameGetDtoAssembler,
                     ExecutionRequestConfiguration executionRequestConfiguration) {
        this.scriptConfiguration = scriptConfiguration;
        this.scriptDtoResourceAssembler = scriptDtoResourceAssembler;
        this.scriptGlobalDtoResourceAssembler = scriptGlobalDtoResourceAssembler;
        this.scriptByNameGetDtoAssembler = scriptByNameGetDtoAssembler;
        this.executionRequestConfiguration = executionRequestConfiguration;
    }

    @GetMapping("")
    public HalMultipleEmbeddedResource<ScriptGlobalDto> getAll() {
        List<Script> scripts = scriptConfiguration.getAll();
        return new HalMultipleEmbeddedResource<>(scripts.stream()
                .filter(distinctByKey(Script::getName))
                .map(script -> scriptGlobalDtoResourceAssembler.toResource(Collections.singletonList(script)))
                .collect(Collectors.toList()));
    }

    @GetMapping("/{name}")
    public ResponseEntity<ScriptByNameDto> getByName(@PathVariable String name) {
        List<Script> script;
        script = scriptConfiguration.getByName(name);
        return ResponseEntity.ok(scriptByNameGetDtoAssembler.toResource(script));
    }

    @GetMapping("/{name}/{version}")
    public ScriptDto get(@PathVariable String name, @PathVariable Long version) throws MetadataDoesNotExistException {
        return scriptConfiguration.get(new ScriptKey(IdentifierTools.getScriptIdentifier(name), version))
                .map(scriptDtoResourceAssembler::toResource)
                .orElseThrow(() -> new MetadataDoesNotExistException(new ScriptKey(IdentifierTools.getScriptIdentifier(name), version)));
    }

    @PostMapping("/")
    public ScriptDto post(@Valid @RequestBody ScriptDto script) throws MetadataAlreadyExistsException {
        scriptConfiguration.insert(script.convertToEntity());
        return scriptDtoResourceAssembler.toResource(script.convertToEntity());
    }

    @PostMapping("/{name}/{version}/execute")
    public ResponseEntity executeScript(@Valid @RequestBody ScriptExecutionDto scriptExecutionDto, @PathVariable String name, @PathVariable Long version) throws MetadataAlreadyExistsException {
        try {
            ExecutionRequest executionRequest = new ExecutionRequestBuilder()
                    .context(scriptExecutionDto.getEnvironment())
                    .scope("script")
                    .name(scriptExecutionDto.getScript())
                    .build();
            ScriptExecutionRequest scriptExecutionRequest = new ScriptExecutionRequestBuilder("script")
                    .scriptName(scriptExecutionDto.getScript())
                    .scriptVersion(scriptExecutionDto.getVersion())
                    .environment(scriptExecutionDto.getEnvironment())
                    .parameters(scriptExecutionDto.getParameters().stream().collect(Collectors.toMap(ScriptParameter::getName, ScriptParameter::getValue)))
                    .executionRequestKey(executionRequest.getMetadataKey())
                    .build();
            executionRequest.setScriptExecutionRequests(Collections.singletonList(scriptExecutionRequest));
            executionRequestConfiguration.insert(executionRequest);

            return ResponseEntity.ok().body(scriptExecutionRequest.getMetadataKey().getId());
        } catch (ScriptExecutionRequestBuilderException | ExecutionRequestBuilderException e) {
            return ResponseEntity.badRequest().build();
        }

    }

    @PutMapping("")
    public HalMultipleEmbeddedResource<ScriptDto> putAll(@Valid @RequestBody List<ScriptDto> scriptDtos) throws MetadataDoesNotExistException {
        HalMultipleEmbeddedResource<ScriptDto> halMultipleEmbeddedResource = new HalMultipleEmbeddedResource<>();
        for (ScriptDto scriptDto : scriptDtos) {
            scriptConfiguration.update(scriptDto.convertToEntity());
            halMultipleEmbeddedResource.embedResource(scriptDto);
            halMultipleEmbeddedResource.add(linkTo(methodOn(ScriptController.class)
                    .getByName(scriptDto.getName()))
                    .withRel(scriptDto.getName()));
        }

        return halMultipleEmbeddedResource;
    }

    @PutMapping("/{name}/{version}")
    public ScriptDto put(@PathVariable String name, @PathVariable Long version,
                         @RequestBody ScriptDto script) throws MetadataDoesNotExistException {
        if (!script.getName().equals(name)) {
            throw new DataBadRequestException(name);
        }
        scriptConfiguration.update(script.convertToEntity());
        return scriptDtoResourceAssembler.toResource(script.convertToEntity());

    }

    @DeleteMapping("{name}")
    public ResponseEntity<?> deleteByName(@PathVariable String name) {
        scriptConfiguration.deleteByName(name);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{name}/{version}")
    public ResponseEntity<?> delete(@PathVariable String name, Long version) throws MetadataDoesNotExistException {
        scriptConfiguration.delete(new ScriptKey(IdentifierTools.getScriptIdentifier(name), version));
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}