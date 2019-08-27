package io.metadew.iesi.server.rest.controller;

import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.launch.operation.ScriptLaunchOperation;
import io.metadew.iesi.metadata.configuration.script.ScriptConfiguration;
import io.metadew.iesi.metadata.configuration.script.exception.ScriptAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.script.exception.ScriptDoesNotExistException;
import io.metadew.iesi.metadata.definition.Request;
import io.metadew.iesi.metadata.definition.RequestParameter;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequestBuilder;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequestBuilderException;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequestBuilder;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequestBuilderException;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.metadata.definition.script.ScriptParameter;
import io.metadew.iesi.runtime.ExecutorService;
import io.metadew.iesi.script.ScriptExecutionBuildException;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.execution.ScriptExecutionBuilder;
import io.metadew.iesi.server.rest.error.DataBadRequestException;
import io.metadew.iesi.server.rest.error.DataNotFoundException;
import io.metadew.iesi.server.rest.error.GetListNullProperties;
import io.metadew.iesi.server.rest.error.GetNullProperties;
import io.metadew.iesi.server.rest.pagination.ScriptCriteria;
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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static io.metadew.iesi.server.rest.helper.Filter.distinctByKey;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
public class ScriptController {

    private ExecutorService executorService;
    private ScriptConfiguration scriptConfiguration;

    private final GetNullProperties getNullProperties;

    private final GetListNullProperties getListNullProperties;


    private final ScriptPagination scriptPagination;

    @Autowired
    ScriptController(ScriptConfiguration scriptConfiguration, GetNullProperties getNullProperties, ScriptDtoResourceAssembler scriptDtoResourceAssembler,
                     GetListNullProperties getListNullProperties, ScriptPagination scriptPagination, ExecutorService executorService) {
        this.scriptPagination = scriptPagination;
        this.scriptConfiguration = scriptConfiguration;
        this.getListNullProperties = getListNullProperties;
        this.getNullProperties = getNullProperties;
        this.scriptDtoResourceAssembler = scriptDtoResourceAssembler;
        this.executorService = executorService;
    }

    @Autowired
    private ScriptByNameDtoAssembler scriptByNameGetDtoAssembler;
    @Autowired
    private ScriptDtoResourceAssembler scriptDtoResourceAssembler;
    @Autowired
    private ScriptGlobalDtoResourceAssembler scriptGlobalDtoResourceAssembler;

    @GetMapping("/scripts")
    public HalMultipleEmbeddedResource<ScriptGlobalDto> getAllScripts(@Valid ScriptCriteria scriptCriteria) {
        List<Script> scripts = scriptConfiguration.getAll();
        List<Script> pagination = scriptPagination.search(scripts, scriptCriteria);
        return new HalMultipleEmbeddedResource<>(pagination.stream()
                .filter(distinctByKey(Script :: getName))
                .map(script -> scriptGlobalDtoResourceAssembler.toResource(Collections.singletonList(script)))
                .collect(Collectors.toList()));
    }

	@GetMapping("/scripts/{name}")
	public ResponseEntity<ScriptByNameDto> getByNameScript(@PathVariable String name) {
        List<Script> script;
        try {
            script = scriptConfiguration.getByName(name);
        } catch (SQLException e) {
            throw new DataNotFoundException(name);
        }
        if (script.isEmpty()) {
			throw new DataNotFoundException(name);
		}
		return ResponseEntity.ok(scriptByNameGetDtoAssembler
				.toResource(script));
}


    @GetMapping("/scripts/{name}/{version}")
    public ScriptDto getScriptsAndVersion(@PathVariable String name, @PathVariable Long version) {
        return scriptConfiguration.get(name, version)
                .map(scriptDtoResourceAssembler :: toResource)
                .orElseThrow(() -> new DataNotFoundException(name, version));
    }

    @PostMapping("/scripts")
    public ScriptDto postScript(@Valid @RequestBody ScriptDto script) {
		getNullProperties.getNullScript(script);
        try {
            scriptConfiguration.insert(script.convertToEntity());
            return scriptDtoResourceAssembler.toResource(script.convertToEntity());
        } catch (ScriptAlreadyExistsException | SQLException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Script " + script.getName() + " already exists");
        }
    }

    @PostMapping("/scripts/{name}/{version}/execute")
    public ResponseEntity postExecuteScript (@Valid @RequestBody ScriptExecutionDto scriptExecutionDto, @PathVariable String name, @PathVariable String version){
		try {
            ExecutionRequest executionRequest = new ExecutionRequestBuilder()
                    .name("on-demand")
                    .scope("REST")
                    .context("script")
                    .build();
            ScriptExecutionRequest scriptExecutionRequest = new ScriptExecutionRequestBuilder("off")
                    .scriptName(scriptExecutionDto.getScript())
                    .scriptVersion(scriptExecutionDto.getVersion())
                    .environment(scriptExecutionDto.getEnvironment())
                    .parameters(scriptExecutionDto.getParameters().stream().collect(Collectors.toMap(ScriptParameter::getName, ScriptParameter::getValue)))
                    .executionRequestKey(executionRequest.getMetadataKey())
                    .build();
            executionRequest.setScriptExecutionRequests(Collections.singletonList(scriptExecutionRequest));

            return ResponseEntity.ok().body(executionRequest.getMetadataKey().getId());
        } catch (ExecutionRequestBuilderException | ScriptExecutionRequestBuilderException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }

    }

    @PutMapping("/scripts")
    public HalMultipleEmbeddedResource<ScriptDto> putAllConnections(@Valid @RequestBody List<ScriptDto> scriptDtos) {
        HalMultipleEmbeddedResource<ScriptDto> halMultipleEmbeddedResource = new HalMultipleEmbeddedResource<>();
//		getListNullProperties.getNullScript(scriptDtos);
        for (ScriptDto scriptDto : scriptDtos) {
            try {
                scriptConfiguration.update(scriptDto.convertToEntity());
                halMultipleEmbeddedResource.embedResource(scriptDto);
                halMultipleEmbeddedResource.add(linkTo(methodOn(ScriptController.class)
                        .getByNameScript(scriptDto.getName()))
                        .withRel(scriptDto.getName()));
            } catch (ScriptDoesNotExistException | SQLException e) {
                e.printStackTrace();
                throw new DataNotFoundException(scriptDto.getName());
            }
        }

        return halMultipleEmbeddedResource;
    }

    @PutMapping("/scripts/{name}/{version}")
    public ScriptDto putScripts(@PathVariable String name, @PathVariable Long version,
                                @RequestBody ScriptDto script) {
//		getNullProperties.getNullScript(script);
		if (!script.getName().equals(name) ) {
			throw new DataBadRequestException(name);
		} else if (!scriptConfiguration.get(name, version).isPresent()){
			throw new DataNotFoundException(name);
		}
        try {
            scriptConfiguration.update(script.convertToEntity());
            return scriptDtoResourceAssembler.toResource(script.convertToEntity());
        } catch (ScriptDoesNotExistException | SQLException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }

    }

    @DeleteMapping("scripts/{name}")
    public ResponseEntity<?> deleteScript(@PathVariable String name) {
        try {
            scriptConfiguration.deleteByName(name);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (ScriptDoesNotExistException | SQLException e) {
            e.printStackTrace();
            throw new DataNotFoundException(name);
        }
    }

    @DeleteMapping("/scripts/{name}/{version}")
    public ResponseEntity<?> deleteByNameScriptAndVersion(@PathVariable String name, Long version) {
        Script script = scriptConfiguration.get(name, version).orElseThrow(() -> new DataNotFoundException(name));
        try {
            scriptConfiguration.delete(script);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (ScriptDoesNotExistException | SQLException e) {
            throw new DataNotFoundException(name);
        }
    }
}