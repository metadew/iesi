package io.metadew.iesi.server.rest.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.metadew.iesi.metadata.configuration.ScriptConfiguration;
import io.metadew.iesi.metadata.configuration.exception.ScriptAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.ScriptDoesNotExistException;
import io.metadew.iesi.metadata.definition.Component;
import io.metadew.iesi.metadata.definition.Script;
import io.metadew.iesi.server.rest.controller.JsonTransformation.ComponentPost;
import io.metadew.iesi.server.rest.pagination.ScriptCriteria;
import io.metadew.iesi.server.rest.pagination.ScriptRepository;
import io.metadew.iesi.server.rest.ressource.component.ComponentResources;
import io.metadew.iesi.server.rest.ressource.script.ScriptResource;
import io.metadew.iesi.server.rest.ressource.script.ScriptResources;

//PAS DE DELETE ALL
@RestController
public class ScriptController {

	private ScriptConfiguration scriptConfiguration ;

	private final ScriptRepository scriptRepository;

	@Autowired
	ScriptController(ScriptConfiguration scriptConfiguration, ScriptRepository scriptRepository) {
		this.scriptRepository = scriptRepository;
		this.scriptConfiguration = scriptConfiguration;
	}

	@GetMapping("/scripts")
	public ResponseEntity<ScriptResources> getAll(@Valid ScriptCriteria scriptCriteria) {
		List<Script> scripts = scriptConfiguration.getAllScripts();
		List<Script> pagination = scriptRepository.search(scripts, scriptCriteria);
		final ScriptResources resource = new ScriptResources(pagination);
		return ResponseEntity.status(HttpStatus.OK).body(resource);
	}

	@GetMapping("/scripts/{name}")
	public ResponseEntity<ScriptResources> getByNameScript(@PathVariable String name) {
		List<Script> script = scriptConfiguration.getScriptByName(name);
		if (!script.isEmpty()) {
			final ScriptResources resource = new ScriptResources(script);
			return ResponseEntity.status(HttpStatus.OK).body(resource);
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
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
	public ResponseEntity<ScriptResource> postScript(@Valid @RequestBody Script script) {
		try {
			scriptConfiguration.insertScript(script);
			final ScriptResource resource = new ScriptResource(script, null);
			return ResponseEntity.status(HttpStatus.OK).body(resource);
		} catch (ScriptAlreadyExistsException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
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
