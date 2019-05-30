package io.metadew.iesi.server.rest.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

import io.metadew.iesi.metadata.configuration.ImpersonationConfiguration;
import io.metadew.iesi.metadata.configuration.exception.ImpersonationAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.ImpersonationDoesNotExistException;
import io.metadew.iesi.metadata.definition.Impersonation;
import io.metadew.iesi.server.rest.pagination.ImpersonationCriteria;
import io.metadew.iesi.server.rest.pagination.ImpersonationRepository;
import io.metadew.iesi.server.rest.ressource.impersonation.ImpersonationResource;
import io.metadew.iesi.server.rest.ressource.impersonation.ImpersonationResources;

@RestController
public class ImpersonationController {

	private ImpersonationConfiguration impersonationConfiguration;
	private final ImpersonationRepository impersonationRepository;

	@Autowired
	ImpersonationController(ImpersonationConfiguration impersonationConfiguration, ImpersonationRepository impersonationRepository) {
		this.impersonationRepository = impersonationRepository;
		this.impersonationConfiguration = impersonationConfiguration;
	}

	@GetMapping("/impersonations")
	public ResponseEntity<ImpersonationResources> getAllImpersonation(
			@Valid ImpersonationCriteria impersonationCriteria) {
		List<Impersonation> impersonation = impersonationConfiguration.getAllImpersonations();
		List<Impersonation> pagination = impersonationRepository.search(impersonation, impersonationCriteria);
		final ImpersonationResources resource = new ImpersonationResources(pagination);
		return ResponseEntity.status(HttpStatus.OK).body(resource);
	}

	@GetMapping("/impersonations/{name}")
	public ResponseEntity<ImpersonationResource> getByName(@PathVariable String name) {
		Optional<Impersonation> impersonation = impersonationConfiguration.getImpersonation(name);
		if (!impersonation.isPresent()) {
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Impersonation not found");
			return new ResponseEntity<ImpersonationResource>(headers, HttpStatus.NOT_FOUND);
		}
		Impersonation newImpersonation = impersonation.orElse(null);
		final ImpersonationResource resource = new ImpersonationResource(newImpersonation, name);
		return ResponseEntity.status(HttpStatus.OK).body(resource);
	}

	@PostMapping("/impersonations")
	public ResponseEntity<ImpersonationResource> postImpersonation(@Valid @RequestBody Impersonation impersonation)
			throws ImpersonationAlreadyExistsException {
		impersonationConfiguration.insertImpersonation(impersonation);
		final ImpersonationResource resource = new ImpersonationResource(impersonation, null);
		return ResponseEntity.status(HttpStatus.OK).body(resource);

	}

	@PutMapping("/impersonations/{name}")
	public ResponseEntity<ImpersonationResource> putImpersonation(@PathVariable String name,
			@RequestBody Impersonation impersonation) throws ImpersonationDoesNotExistException {
		if (!impersonation.getName().equals(name)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		impersonationConfiguration.updateImpersonation(impersonation);
		Optional<Impersonation> updatedEnvironment = impersonationConfiguration.getImpersonation(name);
		Impersonation newupdatedEnvironment = updatedEnvironment.orElse(null);
		final ImpersonationResource resource = new ImpersonationResource(newupdatedEnvironment, name);

		return ResponseEntity.status(HttpStatus.OK).body(resource);
	}

	@PutMapping("/impersonations")
	public ResponseEntity<ImpersonationResources> putAllImpersonation(
			@Valid @RequestBody List<Impersonation> impersonations) throws ImpersonationDoesNotExistException {
		List<Impersonation> updatedImpersonation = new ArrayList<Impersonation>();
		for (Impersonation impersonation : impersonations) {
			impersonationConfiguration.updateImpersonation(impersonation);
			Optional.ofNullable(impersonation).ifPresent(updatedImpersonation::add);
		}
		final ImpersonationResources resource = new ImpersonationResources(updatedImpersonation);

		return ResponseEntity.status(HttpStatus.OK).body(resource);
	}

	@DeleteMapping("/impersonations")
	public ResponseEntity<?> deleteAllImpersonation() {
		impersonationConfiguration.deleteAllImpersonations();
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@DeleteMapping("/impersonations/{name}")
	public ResponseEntity<?> deleteByNameImpersonation(@PathVariable String name) {
		Optional<Impersonation> impersonation = impersonationConfiguration.getImpersonation(name);
		if (impersonation.isPresent()) {
			impersonationConfiguration.deleteImpersonation(name);
			return ResponseEntity.status(HttpStatus.OK).build();
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}

}
