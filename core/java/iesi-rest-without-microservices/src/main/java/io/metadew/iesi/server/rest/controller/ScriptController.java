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
import io.metadew.iesi.server.rest.error.DataBadRequestException;
import io.metadew.iesi.server.rest.error.DataNotFoundException;
import io.metadew.iesi.server.rest.pagination.ScriptPagination;
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
import org.springframework.web.server.ResponseStatusException;

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
                     ScriptPagination scriptPagination, ExecutionRequestConfiguration executionRequestConfiguration) {
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
        if (script.isEmpty()) {
            throw new DataNotFoundException(name);
        }
        return ResponseEntity.ok(scriptByNameGetDtoAssembler.toResource(script));
    }


    @GetMapping("/{name}/{version}")
    public ScriptDto get(@PathVariable String name, @PathVariable Long version) {
        return scriptConfiguration.get(new ScriptKey(name, version))
                .map(scriptDtoResourceAssembler::toResource)
                .orElseThrow(() -> new DataNotFoundException(name, version));
    }

    @PostMapping("/")
    public ScriptDto post(@Valid @RequestBody ScriptDto script) {
        try {
            scriptConfiguration.insert(script.convertToEntity());
            return scriptDtoResourceAssembler.toResource(script.convertToEntity());
        } catch (MetadataAlreadyExistsException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Script " + script.getName() + " already exists");
        }
    }

    @PostMapping("/{name}/{version}/execute")
    public ResponseEntity executeScript(@Valid @RequestBody ScriptExecutionDto scriptExecutionDto, @PathVariable String name, @PathVariable Long version) {
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
        } catch (ScriptExecutionRequestBuilderException | ExecutionRequestBuilderException | MetadataAlreadyExistsException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }

    }

    @PutMapping("")
    public HalMultipleEmbeddedResource<ScriptDto> putAll(@Valid @RequestBody List<ScriptDto> scriptDtos) {
        HalMultipleEmbeddedResource<ScriptDto> halMultipleEmbeddedResource = new HalMultipleEmbeddedResource<>();
        for (ScriptDto scriptDto : scriptDtos) {
            try {
                scriptConfiguration.update(scriptDto.convertToEntity());
                halMultipleEmbeddedResource.embedResource(scriptDto);
                halMultipleEmbeddedResource.add(linkTo(methodOn(ScriptController.class)
                        .getByName(scriptDto.getName()))
                        .withRel(scriptDto.getName()));
            } catch (MetadataDoesNotExistException e) {
                e.printStackTrace();
                throw new DataNotFoundException(scriptDto.getName());
            }
        }

        return halMultipleEmbeddedResource;
    }

    @PutMapping("/{name}/{version}")
    public ScriptDto put(@PathVariable String name, @PathVariable Long version,
                         @RequestBody ScriptDto script) {
        if (!script.getName().equals(name)) {
            throw new DataBadRequestException(name);
        } else if (!scriptConfiguration.get(new ScriptKey(name, version)).isPresent()) {
            throw new DataNotFoundException(name);
        }
        try {
            scriptConfiguration.update(script.convertToEntity());
            return scriptDtoResourceAssembler.toResource(script.convertToEntity());
        } catch (MetadataDoesNotExistException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }

    }

    @DeleteMapping("{name}")
    public ResponseEntity<?> deleteByName(@PathVariable String name) {
        scriptConfiguration.deleteByName(name);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{name}/{version}")
    public ResponseEntity<?> delete(@PathVariable String name, Long version) {
        Script script = scriptConfiguration.get(new ScriptKey(name, version)).orElseThrow(() -> new DataNotFoundException(name));
        try {
            scriptConfiguration.delete(script.getMetadataKey());
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (MetadataDoesNotExistException e) {
            throw new DataNotFoundException(name);
        }
    }
}