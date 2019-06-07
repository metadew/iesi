package io.metadew.iesi.server.rest.controller;

import io.metadew.iesi.metadata.configuration.ScriptConfiguration;
import io.metadew.iesi.metadata.configuration.exception.ScriptAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.ScriptDoesNotExistException;
import io.metadew.iesi.metadata.definition.Script;
import io.metadew.iesi.server.rest.error.DataNotFoundException;
import io.metadew.iesi.server.rest.error.GetListNullProperties;
import io.metadew.iesi.server.rest.error.GetNullProperties;
import io.metadew.iesi.server.rest.pagination.ScriptCriteria;
import io.metadew.iesi.server.rest.pagination.ScriptRepository;
import io.metadew.iesi.server.rest.ressource.HalMultipleEmbeddedResource;
import io.metadew.iesi.server.rest.ressource.script.*;
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

	private ScriptConfiguration scriptConfiguration;

	private final GetNullProperties getNullProperties;

	private final GetListNullProperties getListNullProperties;

	private final ScriptRepository scriptRepository;

	@Autowired
	ScriptController(ScriptConfiguration scriptConfiguration,
					 GetNullProperties getNullProperties, ScriptPostByNameDtoResourceAssembler scriptPostByNameDtoResourceAssembler,GetListNullProperties getListNullProperties, ScriptRepository scriptRepository) {
		this.scriptRepository = scriptRepository;
		this.scriptConfiguration = scriptConfiguration;
		this.getListNullProperties = getListNullProperties;
		this.getNullProperties = getNullProperties;
		this.scriptPostByNameDtoResourceAssembler = scriptPostByNameDtoResourceAssembler;
	}
//	@Autowired
//	private ScriptDtoResourceAssembler scriptDtoResourceAssembler;

	@Autowired
	private ScriptByNameDtoResourceAssembler scriptByNameDtoResourceAssembler;
	@Autowired
	private ScriptGetByNameGetDtoAssembler scriptGetByNameGetDtoAssembler;
	@Autowired
	private ScriptPostByNameDtoResourceAssembler scriptPostByNameDtoResourceAssembler;
	@Autowired
	private ScriptGlobalDtoResourceAssembler scriptGlobalDtoResourceAssembler;

	@GetMapping("/scripts")
	public HalMultipleEmbeddedResource<ScriptGlobalDto> getAllScripts(@Valid ScriptCriteria scriptCriteria) {
		return new HalMultipleEmbeddedResource<>(scriptConfiguration.getAllScripts().stream()
				.filter(distinctByKey(Script :: getName))
				.map(script -> scriptGlobalDtoResourceAssembler.toResource(Collections.singletonList(script)))
				.collect(Collectors.toList()));
	}

	@GetMapping("/scripts/{name}")
	public ResponseEntity<ScriptGetByNameDto> getByNameScript(@PathVariable String name) {
		List<Script> script = scriptConfiguration.getScriptByName(name);
		if (script.isEmpty()) {
			throw new DataNotFoundException(name);
		}
		return ResponseEntity.ok(scriptGetByNameGetDtoAssembler
				.toResource(script));
	}


	@GetMapping("/scripts/{name}/{version}")
	public ResponseEntity<ScriptDto> getScriptsAndVersion(@PathVariable String name, @PathVariable Long version) {
		Optional<Script> scripts = scriptConfiguration.getScript(name, version);
		if (!scripts.isPresent()) {
			throw new DataNotFoundException(name, version);
		}
		Script script = scripts.orElse(null);
		return ResponseEntity.ok(scriptPostByNameDtoResourceAssembler.toResource(Collections.singletonList(script)));
	}

	@PostMapping("/scripts")
	public ResponseEntity<ScriptDto> postScript(@Valid @RequestBody ScriptDto script) {
//		getNullProperties.getNullProperties(script);
		try {
			scriptConfiguration.insertScript(script.convertToEntity());
			List<Script> scriptList = java.util.Arrays.asList(script.convertToEntity());
			return ResponseEntity.ok(scriptPostByNameDtoResourceAssembler.toResource(scriptList));
		} catch (ScriptAlreadyExistsException e) {
			e.printStackTrace();
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,
					"Script " + script.getName() + " already exists");
		}
	}

	@PutMapping("/scripts")
	public HalMultipleEmbeddedResource<ScriptDto> putAllConnections(@Valid @RequestBody List<ScriptDto> scriptDtos) {
		HalMultipleEmbeddedResource<ScriptDto> halMultipleEmbeddedResource = new HalMultipleEmbeddedResource<>();
		getListNullProperties.getNullScript(scriptDtos);
		for (ScriptDto scriptDto : scriptDtos) {
			try {
				scriptConfiguration.updateScript(scriptDto.convertToEntity());
				halMultipleEmbeddedResource.embedResource(scriptDto);
				halMultipleEmbeddedResource.add(linkTo(methodOn(ScriptController.class)
						.getByNameScript(scriptDto.getName()))
						.withRel(scriptDto.getName()));
			} catch (ScriptDoesNotExistException e) {
				e.printStackTrace();
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
			}
		}

		return halMultipleEmbeddedResource;
	}

	@PutMapping("/scripts/{name}/{version}")
	public ScriptByNameDto putScripts(@PathVariable String name,@PathVariable Long version,
									  @RequestBody ScriptDto script) {
		getNullProperties.getNullProperties(script);
		if (!script.getName().equals(name) || !script.getVersion().equals(version) ) {
			throw new DataNotFoundException(name);
		}
		try {
			scriptConfiguration.updateScript(script.convertToEntity());
			List<Script> scriptList = java.util.Arrays.asList(script.convertToEntity());
			return scriptByNameDtoResourceAssembler.toResource(scriptList);
		} catch (ScriptDoesNotExistException e) {
			e.printStackTrace();
			return null;
		}

	}
//	@DeleteMapping("scripts")
//	public ResponseEntity<?> deleteAllScripts()
//		
//		scriptConfiguration.deleteScript(script);
//	}

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
