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


@RestController
public class ScriptController {

	private ScriptConfiguration scriptConfiguration ;

	private final GetNullProperties getNullProperties;

	private final GetListNullProperties getListNullProperties;

	private final ScriptRepository scriptRepository;

	@Autowired
	ScriptController(ScriptConfiguration scriptConfiguration,
					 GetNullProperties getNullProperties, GetListNullProperties getListNullProperties,ScriptRepository scriptRepository) {
		this.scriptRepository = scriptRepository;
		this.scriptConfiguration = scriptConfiguration;
		this.getListNullProperties = getListNullProperties;
		this.getNullProperties = getNullProperties;
	}
//	@Autowired
//	private ScriptDtoResourceAssembler scriptDtoResourceAssembler;

	@Autowired
	private ScriptByNameDtoResourceAssembler scriptByNameDtoResourceAssembler;

	@Autowired
	private ScriptGlobalDtoResourceAssembler scriptGlobalDtoResourceAssembler;

	@GetMapping("/scripts")
	public HalMultipleEmbeddedResource<ScriptGlobalDto> getAllScripts(@Valid ScriptCriteria scriptCriteria) {
		return new HalMultipleEmbeddedResource<>(scriptConfiguration.getAllScripts().stream()
				.filter(distinctByKey(Script::getName))
				.map(script -> scriptGlobalDtoResourceAssembler.toResource(Collections.singletonList(script)))
				.collect(Collectors.toList()));
	}

	@GetMapping("/scripts/{name}")
	public ResponseEntity<ScriptByNameDto> getByNameScript(@PathVariable String name) {
		List<Script> script = scriptConfiguration.getScriptByName(name);
		if (script.isEmpty()) {
			throw new DataNotFoundException(name);
		}
		return ResponseEntity.ok(scriptByNameDtoResourceAssembler
				.toResource(script));
	}


	@GetMapping("/scripts/{name}/{version}")
	public ResponseEntity<ScriptResource> getScriptsAndVersion(@PathVariable String name, @PathVariable Long version) {
		Optional<Script> scripts = scriptConfiguration.getScript(name, version);
		if (scripts.isPresent()) {
			Script script = scripts.orElse(null);
			final ScriptResource resource = new ScriptResource(script, null);
			return ResponseEntity.status(HttpStatus.OK).body(resource);
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}

	@PostMapping("/scripts")
	public ResponseEntity<ScriptByNameDto> postScript(@Valid @RequestBody ScriptDto script) {
		try {
			scriptConfiguration.insertScript(script.convertToEntity());

			List<Script> scriptList = java.util.Arrays.asList(script.convertToEntity());
			return ResponseEntity.ok(scriptByNameDtoResourceAssembler.toResource(scriptList));
		} catch (ScriptAlreadyExistsException e) {
			e.printStackTrace();
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,
					"Script " + script.getName() + " already exists");
		}
	}
	@PutMapping("/scripts")
	public ResponseEntity<ScriptResources> putAllScript(@Valid @RequestBody List<Script> scripts)
			throws ScriptDoesNotExistException {
		List<Script> updatedScript = new ArrayList<Script>();
		for (Script script : scripts) {
			scriptConfiguration.updateScript(script);
			Optional.ofNullable(script).ifPresent(updatedScript::add);
			final ScriptResources resource = new ScriptResources(updatedScript);
			return ResponseEntity.status(HttpStatus.OK).body(resource);
		}

		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}

	@PutMapping("/scripts/{name}/{version}")
	public ResponseEntity<ScriptResource> putByNameScriptAndVersion(@PathVariable String name,
			@PathVariable Long version, @Valid @RequestBody Script script) {
		Optional<Script> scripts = scriptConfiguration.getScript(name, version);
		if (scripts.isPresent()) {
			script = scripts.orElse(null);
			try {
				scriptConfiguration.updateScript(script);
				final ScriptResource resource = new ScriptResource(script, null);
				return ResponseEntity.status(HttpStatus.OK).body(resource);
			} catch (ScriptDoesNotExistException e) {
				e.printStackTrace();
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
			}
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
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
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}

	@DeleteMapping("/scripts/{name}/{version}")
	public ResponseEntity<?> deleteByNameScriptAndVersion(@PathVariable String name, Long version) {
		Optional<Script> scripts = scriptConfiguration.getScript(name, version);
		if (scripts.isPresent()) {
			Script script = scripts.orElse(null);
			try {
				scriptConfiguration.deleteScript(script);
				return ResponseEntity.status(HttpStatus.OK).build();
			} catch (ScriptDoesNotExistException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			}
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
	}
}
