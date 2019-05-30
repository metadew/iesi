package io.metadew.iesi.server.rest.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.metadew.iesi.metadata.configuration.ConnectionConfiguration;
import io.metadew.iesi.metadata.configuration.EnvironmentConfiguration;
import io.metadew.iesi.metadata.configuration.exception.EnvironmentAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.EnvironmentDoesNotExistException;
import io.metadew.iesi.metadata.definition.Connection;
import io.metadew.iesi.metadata.definition.Environment;
import io.metadew.iesi.server.rest.controller.JsonTransformation.EnvironmentName;
import io.metadew.iesi.server.rest.pagination.EnvironmentCriteria;
import io.metadew.iesi.server.rest.pagination.EnvironmentRepository;
import io.metadew.iesi.server.rest.ressource.environment.EnvironmentNameResource;
import io.metadew.iesi.server.rest.ressource.environment.EnvironmentResource;
import io.metadew.iesi.server.rest.ressource.environment.EnvironmentResources;

@RestController
public class EnvironmentsController {

	private EnvironmentConfiguration environmentConfiguration;

	private ConnectionConfiguration connectionConfiguration;

	private final EnvironmentRepository environmentRepository;

	@Autowired
	EnvironmentsController(EnvironmentConfiguration environmentConfiguration, ConnectionConfiguration connectionConfiguration,
						   EnvironmentRepository environmentRepository) {
		this.environmentConfiguration = environmentConfiguration;
		this.connectionConfiguration = connectionConfiguration;
		this.environmentRepository = environmentRepository;
	}

//
//	@PreAuthorize("hasRole('')")
//    @PreAuthorize("hasAuthority('AUTHORIZED_USER')")
	@GetMapping("/environments")
	public ResponseEntity<EnvironmentResources> getAll(@Valid EnvironmentCriteria environmentCriteria) {
		List<Environment> environment = environmentConfiguration.getAllEnvironments();
		List<Environment> pagination = environmentRepository.search(environment, environmentCriteria);
		final EnvironmentResources resource = new EnvironmentResources(pagination);
		return ResponseEntity.status(HttpStatus.OK).body(resource);
	}

	@GetMapping("/environments/{name}")

	public ResponseEntity<EnvironmentResource> getByName(@PathVariable String name) {
		Optional<Environment> environment = environmentConfiguration.getEnvironment(name);
		if (!environment.isPresent()) {
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Environment not found");
			return new ResponseEntity<EnvironmentResource>(headers, HttpStatus.NOT_FOUND);
		}
		Environment environmentOptional = environment.orElse(null);
		final EnvironmentResource resource = new EnvironmentResource(environmentOptional, name);
		return ResponseEntity.status(HttpStatus.OK).body(resource);
	}

	@PostMapping("/environments")

	public ResponseEntity<EnvironmentResource> postAllEnvironments(@Valid @RequestBody Environment environment)
			throws EnvironmentAlreadyExistsException {
		environmentConfiguration.insertEnvironment(environment);
		final EnvironmentResource resource = new EnvironmentResource(environment, null);
		return ResponseEntity.status(HttpStatus.OK).body(resource);
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
			@RequestBody Environment environment) throws EnvironmentDoesNotExistException {
		if (!environment.getName().equals(name)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		environmentConfiguration.updateEnvironment(environment);
		Optional<Environment> updatedEnvironment = environmentConfiguration.getEnvironment(name);
		Environment newEnvironment = updatedEnvironment.orElse(null);
		final EnvironmentResource resource = new EnvironmentResource(newEnvironment, name);
		return ResponseEntity.status(HttpStatus.OK).body(resource);
	}

	@GetMapping("/environments/{name}/connections")

	public ResponseEntity<EnvironmentNameResource> getEnvironmentsConnections(@PathVariable String name) {
		List<Connection> connections = connectionConfiguration.getConnections();
		List<Connection> result = connections.stream().filter(connection -> connection.getEnvironment().equals(name))
				.collect(Collectors.toList());
		EnvironmentName environmentName = new EnvironmentName(result);
		final EnvironmentNameResource resource = new EnvironmentNameResource(environmentName);
		return ResponseEntity.status(HttpStatus.OK).body(resource);
	}

	@DeleteMapping("/environments")

	public ResponseEntity<?> deleteAllEnvironments() {
		environmentConfiguration.deleteAllEnvironments();
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@DeleteMapping("environments/{name}")
	public ResponseEntity<?> deleteEnvironments(@PathVariable String name) {
		Optional<Environment> environment = environmentConfiguration.getEnvironment(name);
		if (environment.isPresent()) {
			environmentConfiguration.deleteEnvironment(name);
			return ResponseEntity.status(HttpStatus.OK).build();
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}

}