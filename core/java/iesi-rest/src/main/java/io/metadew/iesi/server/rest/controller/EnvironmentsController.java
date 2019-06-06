package io.metadew.iesi.server.rest.controller;

import io.metadew.iesi.metadata.configuration.ConnectionConfiguration;
import io.metadew.iesi.metadata.configuration.EnvironmentConfiguration;
import io.metadew.iesi.metadata.configuration.exception.EnvironmentAlreadyExistsException;
import io.metadew.iesi.metadata.definition.Connection;
import io.metadew.iesi.metadata.definition.Environment;
import io.metadew.iesi.server.rest.error.CustomGlobalExceptionHandler;
import io.metadew.iesi.server.rest.error.DataNotFoundException;
import io.metadew.iesi.server.rest.error.GetListNullProperties;
import io.metadew.iesi.server.rest.error.GetNullProperties;
import io.metadew.iesi.server.rest.pagination.EnvironmentCriteria;
import io.metadew.iesi.server.rest.pagination.EnvironmentRepository;
import io.metadew.iesi.server.rest.ressource.HalMultipleEmbeddedResource;
import io.metadew.iesi.server.rest.ressource.connection.resource.ConnectionByNameDtoResourceAssembler;
import io.metadew.iesi.server.rest.ressource.connection.resource.ConnectionDtoResourceAssembler;
import io.metadew.iesi.server.rest.ressource.environment.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.metadew.iesi.server.rest.helper.Filter.distinctByKey;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
public class EnvironmentsController {

	private EnvironmentConfiguration environmentConfiguration;

	@Autowired
	private ConnectionDtoResourceAssembler connectionDtoResourceAssembler;

	private ConnectionConfiguration connectionConfiguration;

	private final GetNullProperties getNullProperties;

	@Autowired
	private ConnectionByNameDtoResourceAssembler connectionByNameDtoResourceAssembler;

	private final GetListNullProperties getListNullProperties;

	private final EnvironmentRepository environmentRepository;

	CustomGlobalExceptionHandler customRestExceptionHandler;

	@Autowired
	private EnvironmentGlobalDtoResourceAssembler environmentGlobalDtoResourceAssembler;

	@Autowired
	private EnvironmentByNameDtoResourceAssembler environmentByNameDtoResourceAssembler;

	@Autowired
	EnvironmentsController(EnvironmentConfiguration environmentConfiguration,GetNullProperties getNullProperties, GetListNullProperties getListNullProperties, ConnectionConfiguration connectionConfiguration,
						   EnvironmentRepository environmentRepository) {
		this.environmentConfiguration = environmentConfiguration;
		this.connectionConfiguration = connectionConfiguration;
		this.environmentRepository = environmentRepository;
		this.getListNullProperties = getListNullProperties;
		this.getNullProperties = getNullProperties;
	}

@GetMapping("/environments")
public HalMultipleEmbeddedResource<EnvironmentGlobalDto> getAllEnvironments(@Valid EnvironmentCriteria environmentCriteria) {
	return new HalMultipleEmbeddedResource<>(environmentConfiguration.getAllEnvironments().stream()
			.filter(distinctByKey(Environment:: getName))
			.map(environment -> environmentGlobalDtoResourceAssembler.toResource(Collections.singletonList(environment)))
			.collect(Collectors.toList()));
}
	@GetMapping("/environments/{name}")
	public ResponseEntity<EnvironmentByNameDto> getByName(@PathVariable String name) {
		Optional<Environment> environment = environmentConfiguration.getEnvironment(name);
		if (!environment.isPresent()) {
			throw new DataNotFoundException(name);
		}
		Environment environmentOptional = environment.orElse(null);
		return ResponseEntity.ok(environmentByNameDtoResourceAssembler.toResource(Collections.singletonList(environmentOptional)));
	}
//
	@PostMapping("/environments")
	public ResponseEntity<EnvironmentByNameDto> postAllEnvironments(@Valid @RequestBody EnvironmentDto environment) {
		getNullProperties.getNullProperties(environment);
		try {
			environmentConfiguration.insertEnvironment(environment.convertToEntity());
			List<Environment> environmentList = java.util.Arrays.asList(environment.convertToEntity());
			return ResponseEntity.ok(environmentByNameDtoResourceAssembler.toResource(environmentList));
		} catch (EnvironmentAlreadyExistsException e) {
			e.printStackTrace();
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,
					"Environment " + environment.getName() + " already exists");
		}
	}
//
//@PutMapping("/environments")
//public HalMultipleEmbeddedResource<EnvironmentDto> putAllConnections(@Valid @RequestBody List<EnvironmentDto> environmentDtos) {
//	getListNullProperties.getNullEnvironment(environmentDtos);
//		HalMultipleEmbeddedResource<EnvironmentDto> halMultipleEmbeddedResource = new HalMultipleEmbeddedResource<>();
//	for (EnvironmentDto environmentDto : environmentDtos) {
//		try {
//			EnvironmentDto updatedEnvironmentDto = convertToDto(environmentConfiguration.updateEnvironment(environmentDto.convertToEntity()));
//			halMultipleEmbeddedResource.embedResource(updatedEnvironmentDto);
//			halMultipleEmbeddedResource.add(linkTo(methodOn(EnvironmentsController.class)
//					.getByName(updatedEnvironmentDto.getName()))
//					.withRel(updatedEnvironmentDto.getName()));
//
//		} catch (EnvironmentDoesNotExistException e) {
//
//		} catch (ConnectionAlreadyExistsException e) {
//			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//	}
//	return halMultipleEmbeddedResource;
//}
//
//	@PutMapping("/environments/{name}")
//	public EnvironmentByNameDto putEnvironments(@PathVariable String name,
//												@RequestBody EnvironmentDto environment) {
//		getNullProperties.getNullProperties(environment);
//		if (!environment.getName().equals(name)) {
//			throw new DataNotFoundException(name);
//		}
//		try {
//			return environmentByNameDtoResourceAssembler.toResource(environmentConfiguration.updateEnvironment(environment.convertToEntity()));
//		} catch (EnvironmentDoesNotExistException e) {
//			e.printStackTrace();
//			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//		}
//
//	}
//
@GetMapping("/environments/{name}/connections")
public ResponseEntity<HalMultipleEmbeddedResource> getEnvironmentsConnections(@PathVariable String name) {
	List<Connection> connections = connectionConfiguration.getConnections();
	List<Connection> result = connections.stream().filter(connection -> connection.getEnvironment().equals(name))
			.collect(Collectors.toList());
	if (result.isEmpty()) {
		throw new DataNotFoundException(name);
	}
	EnvironmentName environmentName = new EnvironmentName(result);
	HalMultipleEmbeddedResource halMultipleEmbeddedResource = new HalMultipleEmbeddedResource(Collections.singletonList(environmentName));
	halMultipleEmbeddedResource.add(linkTo(methodOn(EnvironmentsController.class)
					.getByName(connections.get(0).getName()))
					.withRel(connections.get(0).getName()));

	return ResponseEntity.status(HttpStatus.OK).body(halMultipleEmbeddedResource);
}

	@DeleteMapping("/environments")
	public ResponseEntity<?> deleteAllEnvironments() {
		List<Environment> environment = environmentConfiguration.getAllEnvironments();
		if (!environment.isEmpty()) {
			environmentConfiguration.deleteAllEnvironments();
			return ResponseEntity.status(HttpStatus.OK).build();
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}

	@DeleteMapping("environments/{name}")
	public ResponseEntity<?> deleteEnvironments(@PathVariable String name) {
		Optional<Environment> environment = environmentConfiguration.getEnvironment(name);
		if (!environment.isPresent()) {
			throw new DataNotFoundException(name);
		}
		environmentConfiguration.deleteEnvironment(name);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

}