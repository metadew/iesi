package io.metadew.iesi.server.rest.controller;

import io.metadew.iesi.metadata.configuration.ConnectionConfiguration;
import io.metadew.iesi.metadata.configuration.EnvironmentConfiguration;
import io.metadew.iesi.metadata.configuration.exception.EnvironmentAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.EnvironmentDoesNotExistException;
import io.metadew.iesi.metadata.definition.Connection;
import io.metadew.iesi.metadata.definition.Environment;
import io.metadew.iesi.server.rest.controller.JsonTransformation.EnvironmentName;
import io.metadew.iesi.server.rest.error.CustomGlobalExceptionHandler;
import io.metadew.iesi.server.rest.error.DataNotFoundException;
import io.metadew.iesi.server.rest.pagination.EnvironmentCriteria;
import io.metadew.iesi.server.rest.pagination.EnvironmentRepository;
import io.metadew.iesi.server.rest.ressource.environment.EnvironmentNameResource;
import io.metadew.iesi.server.rest.ressource.environment.EnvironmentResource;
import io.metadew.iesi.server.rest.ressource.environment.EnvironmentResources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RefreshScope
public class EnvironmentsController {

	private EnvironmentConfiguration environmentConfiguration;

	private ConnectionConfiguration connectionConfiguration;

	private final EnvironmentRepository environmentRepository;

	CustomGlobalExceptionHandler customRestExceptionHandler;

	@Autowired
	EnvironmentsController(EnvironmentConfiguration environmentConfiguration, ConnectionConfiguration connectionConfiguration,
						   EnvironmentRepository environmentRepository) {
		this.environmentConfiguration = environmentConfiguration;
		this.connectionConfiguration = connectionConfiguration;
		this.environmentRepository = environmentRepository;
	}

	@GetMapping("/environments")
	public ResponseEntity<EnvironmentResources> getAll(@Valid EnvironmentCriteria environmentCriteria) {
		List<Environment> environment = environmentConfiguration.getAllEnvironments();
		List<Environment> pagination = environmentRepository.search(environment, environmentCriteria);
		final EnvironmentResources resource = new EnvironmentResources(pagination);
		return ResponseEntity.status(HttpStatus.OK).body(resource);
	}

	@GetMapping("/environments/{name}")

	public ResponseEntity<EnvironmentResource> getByName(@PathVariable String name, Error error) {
		Optional<Environment> environment = environmentConfiguration.getEnvironment(name);
		if (!environment.isPresent()) {
			throw new DataNotFoundException(name);
		}
		Environment environmentOptional = environment.orElse(null);
		final EnvironmentResource resource = new EnvironmentResource(environmentOptional, name);
		return ResponseEntity.status(HttpStatus.OK).body(resource);
	}

	@PostMapping("/environments")

	public ResponseEntity<EnvironmentResource> postAllEnvironments(@Valid @RequestBody Environment environment) {
		try {
			environmentConfiguration.insertEnvironment(environment);
			final EnvironmentResource resource = new EnvironmentResource(environment, null);
			return ResponseEntity.status(HttpStatus.OK).body(resource);
		} catch (EnvironmentAlreadyExistsException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	@PutMapping("/environments")
	public ResponseEntity<EnvironmentResources> putAllEnvironment(@Valid @RequestBody List<Environment> environments)
			throws EnvironmentDoesNotExistException {
		List<Environment> updatedEnvironment = new ArrayList<Environment>();
		for (Environment environment : environments) {
			environmentConfiguration.updateEnvironment(environment);
			Optional.ofNullable(environment).ifPresent(updatedEnvironment::add);
		}
		final EnvironmentResources resource = new EnvironmentResources(updatedEnvironment);
		return ResponseEntity.status(HttpStatus.OK).body(resource);
	}

	@PutMapping("/environments/{name}")
	public ResponseEntity<EnvironmentResource> putEnvironments(@PathVariable String name,
			@RequestBody Environment environment) {
		if (!environment.getName().equals(name)) {
			throw new DataNotFoundException(name);
		}
		try {
			environmentConfiguration.updateEnvironment(environment);
			Optional<Environment> updatedEnvironment = environmentConfiguration.getEnvironment(name);
			Environment newEnvironment = updatedEnvironment.orElse(null);
			final EnvironmentResource resource = new EnvironmentResource(newEnvironment, name);
			return ResponseEntity.status(HttpStatus.OK).body(resource);
		} catch (EnvironmentDoesNotExistException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

	}

	@GetMapping("/environments/{name}/connections")

	public ResponseEntity<EnvironmentNameResource> getEnvironmentsConnections(@PathVariable String name) {
		List<Connection> connections = connectionConfiguration.getConnections();
		List<Connection> result = connections.stream().filter(connection -> connection.getEnvironment().equals(name))
				.collect(Collectors.toList());
		if (result.isEmpty()) {
			throw new DataNotFoundException(name);
		}
		EnvironmentName environmentName = new EnvironmentName(result);
		final EnvironmentNameResource resource = new EnvironmentNameResource(environmentName);
		return ResponseEntity.status(HttpStatus.OK).body(resource);
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