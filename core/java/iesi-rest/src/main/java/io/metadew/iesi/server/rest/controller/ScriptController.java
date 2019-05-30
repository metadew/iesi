package io.metadew.iesi.server.rest.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.metadew.iesi.metadata.configuration.ScriptConfiguration;
import io.metadew.iesi.metadata.definition.ListObject;
import io.metadew.iesi.metadata.definition.Script;
import io.metadew.iesi.server.rest.pagination.ScriptCriteria;
import io.metadew.iesi.server.rest.pagination.ScriptRepository;
import io.metadew.iesi.server.rest.ressource.script.ScriptResource;

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
	public ListObject getAll(@Valid ScriptCriteria scripcriteria) {
		ListObject script = scriptConfiguration.getScripts();
//		List<Script> pagination = scriptRepository.search(script,scripcriteria);
		return script;

	}

	@GetMapping("/scripts/{name}")
	public ResponseEntity<ScriptResource> getByNameScript(@PathVariable String name) {
		Script script = scriptConfiguration.getScript(name).get();
		final ScriptResource resource = new ScriptResource(script, null);
		return ResponseEntity.status(HttpStatus.OK).body(resource);
	}
	
	@GetMapping("/scripts/{name}/{version}")
	public Script getByNameScriptAndVersion(@PathVariable String name, Long version) {
		Script scriptVersion = scriptConfiguration.getScript(name, version).get();
		return scriptVersion;
	}

//	@PostMapping("/scripts")
//	public ResponseEntity<ScriptResource> postScript(@Valid @RequestBody Script script) {
//		return ResponseEntity.status(HttpStatus.OK).body(script);
//	}

	@PutMapping("/scripts")
	public ListObject putAllScript(@Valid @RequestBody ListObject script) {

		return script;
	}

	@PutMapping("/scripts/{name}/{version}")
	public Script putByNameScriptAndVersion(@PathVariable String name, Long version,
			@Valid @RequestBody Script script) {

		return script;
	}

	@DeleteMapping("scripts")
	public ListObject deleteAllScript(@PathVariable ListObject script) {

		script = scriptConfiguration.getScripts();
		return script;
	}

	@DeleteMapping("scripts/{name}")
	public Script deleteScript(@PathVariable String name) {

		Script scriptVersion = scriptConfiguration.getScript(name).get();
		return scriptVersion;
	}

	@DeleteMapping("/scripts/{name}/{version}")
	public Script deleteByNameScriptAndVersion(@PathVariable String name, Long version) {

		Script scriptVersion = scriptConfiguration.getScript(name, version).get();
		return scriptVersion;
	}
}
