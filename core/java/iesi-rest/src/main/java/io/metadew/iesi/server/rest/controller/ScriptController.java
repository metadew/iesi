//package io.metadew.iesi.server.rest.controller;
//
//
//
//import io.metadew.iesi.metadata.configuration.ScriptConfiguration;
//import io.metadew.iesi.metadata.configuration.exception.ScriptAlreadyExistsException;
//import io.metadew.iesi.metadata.configuration.exception.ScriptDoesNotExistException;
//import io.metadew.iesi.metadata.definition.Script;
//import io.metadew.iesi.server.rest.pagination.ScriptCriteria;
//import io.metadew.iesi.server.rest.ressource.HalMultipleEmbeddedResource;
//import io.metadew.iesi.server.rest.ressource.script.dto.ScriptByNameDto;
//import io.metadew.iesi.server.rest.ressource.script.dto.ScriptDto;
//import io.metadew.iesi.server.rest.ressource.script.dto.ScriptGlobalDto;
//import io.metadew.iesi.server.rest.ressource.script.resource.ScriptByNameDtoResourceAssembler;
//import io.metadew.iesi.server.rest.ressource.script.resource.ScriptDtoResourceAssembler;
//import io.metadew.iesi.server.rest.ressource.script.resource.ScriptGlobalDtoResourceAssembler;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.server.ResponseStatusException;
//
//import javax.validation.Valid;
//import java.text.MessageFormat;
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//import static io.metadew.iesi.server.rest.helper.Filter.distinctByKey;
//import static io.metadew.iesi.server.rest.ressource.script.dto.ScriptDto.convertToDto;
//import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
//import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
//
//@RestController
//@RequestMapping("/scripts")
//public class ScriptController {
//
//	private ScriptConfiguration scriptConfiguration;
//
//	@Autowired
//	ScriptController(ScriptConfiguration scriptConfiguration) {
//		this.scriptConfiguration = scriptConfiguration;
//	}
//
//	@Autowired
//	private ScriptDtoResourceAssembler scriptDtoResourceAssembler;
//
//	@Autowired
//	private ScriptByNameDtoResourceAssembler scriptByNameDtoResourceAssembler;
//
//	@Autowired
//	private ScriptGlobalDtoResourceAssembler scriptGlobalDtoResourceAssembler;
//
//	@GetMapping("")
//	public HalMultipleEmbeddedResource<ScriptGlobalDto> getAllScripts(@Valid ScriptCriteria scriptCriteria) {
//		return new HalMultipleEmbeddedResource<>(scriptConfiguration.getScripts().stream()
//				.filter(distinctByKey(Script::getName))
//				.map(script -> scriptGlobalDtoResourceAssembler.toResource(Collections.singletonList(script)))
//				.collect(Collectors.toList()));
//	}
//
//	@GetMapping("/{name}")
//	public ScriptByNameDto getByName(@PathVariable String name) {
//		List<Script> scripts = scriptConfiguration.getScriptByName(name);
//		if (scripts.isEmpty()) {
//			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
//		}
//
//		return scriptByNameDtoResourceAssembler.toResource(scripts);
//	}
//
//	@GetMapping("/{name}/{version}")
//	public ScriptDto getByNameandEnvironment(@PathVariable String name,
//											 @PathVariable Long version) {
//		Optional<Script> script = scriptConfiguration.getScript(name, version);
//		return script
//				.map(scriptDtoResourceAssembler::toResource)
//				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
//	}
//
//	@PostMapping("")
//	public ScriptDto postAllScripts(@Valid @RequestBody ScriptDto scriptDto) {
//		try {
//			scriptConfiguration.insertScript(scriptDto.convertToEntity());
//		} catch (ScriptAlreadyExistsException e) {
//			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
//					MessageFormat.format("Script {0}-{1} already exists", scriptDto.getName(), scriptDto.getEnvironment()));
//		}
//		return scriptConfiguration.getScript(scriptDto.getName(), scriptDto.getEnvironment())
//				.map(scriptDtoResourceAssembler::toResource)
//				.orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));
//	}
//
//	@PutMapping("")
//	public HalMultipleEmbeddedResource<ScriptDto> putAllScripts(@Valid @RequestBody List<ScriptDto> scriptDtos) {
//		HalMultipleEmbeddedResource<ScriptDto> halMultipleEmbeddedResource = new HalMultipleEmbeddedResource<>();
//		for (ScriptDto scriptDto : scriptDtos) {
//			try {
//				ScriptDto updatedScriptDto = convertToDto(scriptConfiguration.updateScript(scriptDto.convertToEntity()));
//				halMultipleEmbeddedResource.embedResource(updatedScriptDto);
//				halMultipleEmbeddedResource.add(linkTo(methodOn(ScriptController.class)
//						.getByNameandEnvironment(updatedScriptDto.getName(), updatedScriptDto.getEnvironment()))
//						.withRel(updatedScriptDto.getName() + ":" + updatedScriptDto.getEnvironment()));
//
//			} catch (ScriptDoesNotExistException e) {
//				throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
//						MessageFormat.format("Script {0}-{1} does not exists", scriptDto.getName(), scriptDto.getEnvironment()));
//			} catch (ScriptAlreadyExistsException e) {
//				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
//			}
//		}
//		return halMultipleEmbeddedResource;
//	}
//
//	@PutMapping("/{name}/{version}")
//	public ScriptDto putScripts(@PathVariable String name,
//								@PathVariable Long version, @RequestBody ScriptDto scriptDto) {
//		if (!scriptDto.getName().equals(name) || !scriptDto.getEnvironment().equals(version)) {
//			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
//					MessageFormat.format("Name ''{0}'' and version ''{1}'' in url do not match name and version in body",
//							name, version));
//		}
//		try {
//			return scriptDtoResourceAssembler.toResource(scriptConfiguration.updateScript(scriptDto.convertToEntity()));
//		} catch (ScriptDoesNotExistException e) {
//			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
//					MessageFormat.format("Script {0}-{1} does not exist", name, version));
//		} catch (ScriptAlreadyExistsException e) {
//			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//	}
//
//
//	@DeleteMapping("/scripts")
//	public ResponseEntity<?> deleteAllScripts() {
//		List<Script> scripts = scriptConfiguration.getScripts();
//		if (!scripts.isEmpty()) {
//			scriptConfiguration.deleteScripts();
//			return ResponseEntity.status(HttpStatus.OK).build();
//		}
//		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
//	}
//
//	@DeleteMapping("/scripts/{name}")
//	public ResponseEntity<?> deleteScriptByName(@PathVariable String name) throws ScriptDoesNotExistException {
//		List<Script> scripts = scriptConfiguration.getScriptsByName(name);
//		if (!scripts.isEmpty()) {
//			scriptConfiguration.deleteScriptByName(name);
//			return ResponseEntity.status(HttpStatus.OK).build();
//		}
//		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//	}
//
//	@DeleteMapping("/scripts/{name}/{version}")
//	public ResponseEntity<?> deleteScriptsAndVersion(@PathVariable String name, @PathVariable Long version)
//			throws ScriptDoesNotExistException {
//		Optional<Script> scripts = scriptConfiguration.getScript(name, version);
//		if (scripts.isPresent()) {
//			Script script = scripts.orElse(null);
//			scriptConfiguration.deleteScript(script);
//			return ResponseEntity.status(HttpStatus.OK).build();
//		}
//		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//	}
//}