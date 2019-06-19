package io.metadew.iesi.server.rest.controller;

import io.metadew.iesi.framework.definition.Framework;
import io.metadew.iesi.framework.definition.FrameworkInitializationFile;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.launch.ScriptLauncher;
import io.metadew.iesi.launch.operation.ScriptLaunchOperation;
import io.metadew.iesi.metadata.configuration.ScriptConfiguration;
import io.metadew.iesi.metadata.configuration.exception.ScriptAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.ScriptDoesNotExistException;
import io.metadew.iesi.metadata.definition.Request;
import io.metadew.iesi.metadata.definition.RequestParameter;
import io.metadew.iesi.metadata.definition.Script;
import io.metadew.iesi.server.rest.error.DataBadRequestException;
import io.metadew.iesi.server.rest.error.DataNotFoundException;
import io.metadew.iesi.server.rest.error.GetListNullProperties;
import io.metadew.iesi.server.rest.error.GetNullProperties;
import io.metadew.iesi.server.rest.pagination.ScriptCriteria;
import io.metadew.iesi.server.rest.pagination.ScriptPagination;
import io.metadew.iesi.server.rest.resource.HalMultipleEmbeddedResource;
import io.metadew.iesi.server.rest.resource.script.dto.*;
import io.metadew.iesi.server.rest.resource.script.resource.ScriptByNameDtoAssembler;
import io.metadew.iesi.server.rest.resource.script.resource.ScriptDtoResourceAssembler;
import io.metadew.iesi.server.rest.resource.script.resource.ScriptGlobalDtoResourceAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.metadew.iesi.server.rest.helper.Filter.distinctByKey;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
public class ScriptController {

    private final FrameworkInstance frameworkInstance;
    private ScriptConfiguration scriptConfiguration;

    private final GetNullProperties getNullProperties;

    private final GetListNullProperties getListNullProperties;

    private final ScriptPagination scriptPagination;

    @Autowired
    ScriptController(ScriptConfiguration scriptConfiguration,
                     GetNullProperties getNullProperties, ScriptDtoResourceAssembler scriptDtoResourceAssembler, GetListNullProperties getListNullProperties, ScriptPagination scriptPagination, FrameworkInstance frameworkInstance) {
        this.scriptPagination = scriptPagination;
        this.scriptConfiguration = scriptConfiguration;
        this.getListNullProperties = getListNullProperties;
        this.getNullProperties = getNullProperties;
        this.scriptDtoResourceAssembler = scriptDtoResourceAssembler;
        this.frameworkInstance = frameworkInstance;
    }

    @Autowired
    private ScriptByNameDtoAssembler scriptByNameGetDtoAssembler;
    @Autowired
    private ScriptDtoResourceAssembler scriptDtoResourceAssembler;
    @Autowired
    private ScriptGlobalDtoResourceAssembler scriptGlobalDtoResourceAssembler;

    @GetMapping("/scripts")
    public HalMultipleEmbeddedResource<ScriptGlobalDto> getAllScripts(@Valid ScriptCriteria scriptCriteria) {
        List<Script> scripts = scriptConfiguration.getAllScripts();
        List<Script> pagination = scriptPagination.search(scripts, scriptCriteria);
        return new HalMultipleEmbeddedResource<>(pagination.stream()
                .filter(distinctByKey(Script :: getName))
                .map(script -> scriptGlobalDtoResourceAssembler.toResource(Collections.singletonList(script)))
                .collect(Collectors.toList()));
    }

	@GetMapping("/scripts/{name}")
	public ResponseEntity<ScriptByNameDto> getByNameScript(@PathVariable String name) {
		List<Script> script = scriptConfiguration.getScriptByName(name);
		if (script.isEmpty()) {
			throw new DataNotFoundException(name);
		}
		return ResponseEntity.ok(scriptByNameGetDtoAssembler
				.toResource(script));
}


    @GetMapping("/scripts/{name}/{version}")
    public ScriptDto getScriptsAndVersion(@PathVariable String name, @PathVariable Long version) {
        return scriptConfiguration.getScript(name, version)
                .map(scriptDtoResourceAssembler :: toResource)
                .orElseThrow(() -> new DataNotFoundException(name, version));
    }

    @PostMapping("/scripts")
    public ScriptDto postScript(@Valid @RequestBody ScriptDto script) {
		getNullProperties.getNullScript(script);
        try {
            scriptConfiguration.insertScript(script.convertToEntity());
            return scriptDtoResourceAssembler.toResource(script.convertToEntity());
        } catch (ScriptAlreadyExistsException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Script " + script.getName() + " already exists");
        }
    }

    @PostMapping("/scripts/{name}/{version}/execute")
    public ResponseEntity postExecuteScript (@Valid @RequestBody ScriptExecutionDto scriptExecution, @PathVariable String name, @PathVariable String version){
        List<RequestParameter> requestParameters = new ArrayList<>();
		requestParameters.add(new RequestParameter("version", "number", Long.toString(scriptExecution.getVersion())));
		requestParameters.add(new RequestParameter("paramlist", "list", scriptExecution.getParameters().stream().map(scriptParameter -> scriptParameter.getName() + "=" + scriptParameter.getValue()).collect(Collectors.joining(","))));
		requestParameters.add(new RequestParameter("mode", "name", "script"));
		requestParameters.add(new RequestParameter("exit", "flag", Boolean.toString(false)));

		Request request = new Request("script", Long.toString(System.currentTimeMillis()), scriptExecution.getScript(), "", 1, "",scriptExecution.getScript(),scriptExecution.getEnvironment(),"","","",requestParameters);
		ScriptLaunchOperation.execute(frameworkInstance, request);
        return ResponseEntity.ok().build();

    }

    @PutMapping("/scripts")
    public HalMultipleEmbeddedResource<ScriptDto> putAllConnections(@Valid @RequestBody List<ScriptDto> scriptDtos) {
        HalMultipleEmbeddedResource<ScriptDto> halMultipleEmbeddedResource = new HalMultipleEmbeddedResource<>();
//		getListNullProperties.getNullScript(scriptDtos);
        for (ScriptDto scriptDto : scriptDtos) {
            try {
                scriptConfiguration.updateScript(scriptDto.convertToEntity());
                halMultipleEmbeddedResource.embedResource(scriptDto);
                halMultipleEmbeddedResource.add(linkTo(methodOn(ScriptController.class)
                        .getByNameScript(scriptDto.getName()))
                        .withRel(scriptDto.getName()));
            } catch (ScriptDoesNotExistException e) {
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
		} else if (!scriptConfiguration.getScript(name, version).isPresent()){
			throw new DataNotFoundException(name);
		}
        try {
            scriptConfiguration.updateScript(script.convertToEntity());
            return scriptDtoResourceAssembler.toResource(script.convertToEntity());
        } catch (ScriptDoesNotExistException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }

    }

    @DeleteMapping("scripts/{name}")
    public ResponseEntity<?> deleteScript(@PathVariable String name) {
        try {
            scriptConfiguration.deleteScriptByName(name);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (ScriptDoesNotExistException e) {
            e.printStackTrace();
            throw new DataNotFoundException(name);
        }
    }

    @DeleteMapping("/scripts/{name}/{version}")
    public ResponseEntity<?> deleteByNameScriptAndVersion(@PathVariable String name, Long version) {
        Optional<Script> scripts = scriptConfiguration.getScript(name, version);
        try {
            Script script = scripts.orElse(null);
            scriptConfiguration.deleteScript(script);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (ScriptDoesNotExistException e) {
            e.printStackTrace();
            throw new DataNotFoundException(name);

        }
    }
}