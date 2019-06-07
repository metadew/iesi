package io.metadew.iesi.server.rest.controller;


import io.metadew.iesi.metadata.configuration.ImpersonationConfiguration;
import io.metadew.iesi.metadata.configuration.exception.ImpersonationAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.ImpersonationDoesNotExistException;
import io.metadew.iesi.metadata.definition.Impersonation;
import io.metadew.iesi.server.rest.error.DataNotFoundException;
import io.metadew.iesi.server.rest.error.GetListNullProperties;
import io.metadew.iesi.server.rest.error.GetNullProperties;
import io.metadew.iesi.server.rest.pagination.ImpersonationCriteria;
import io.metadew.iesi.server.rest.ressource.HalMultipleEmbeddedResource;
import io.metadew.iesi.server.rest.ressource.impersonation.*;
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
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static io.metadew.iesi.server.rest.helper.Filter.distinctByKey;

@RestController
@RequestMapping("/impersonations")
public class ImpersonationController {

	private ImpersonationConfiguration impersonationConfiguration;
	private final GetListNullProperties getListNullProperties;
	private final GetNullProperties getNullProperties;
	@Autowired
	ImpersonationController(ImpersonationConfiguration impersonationConfiguration,GetNullProperties getNullProperties,
							GetListNullProperties getListNullProperties			) {
		this.impersonationConfiguration = impersonationConfiguration;
		this.getListNullProperties = getListNullProperties;
		this.getNullProperties = getNullProperties;
	}

	@Autowired
	private ImpersonationGlobalDtoResourceAssembler impersonationDtoResourceAssembler;

	@Autowired
	private ImpersonationByNameDtoResourceAssembler impersonationByNameDtoResourceAssembler;

	@Autowired
	private ImpersonationGlobalDtoResourceAssembler impersonationGlobalDtoResourceAssembler;

	@GetMapping("")
	public HalMultipleEmbeddedResource<ImpersonationGlobalDto> getAllImpersonations(@Valid ImpersonationCriteria impersonationCriteria) {
		return new HalMultipleEmbeddedResource<>(impersonationConfiguration.getAllImpersonations().stream()
				.filter(distinctByKey(Impersonation::getName))
				.map(impersonation -> impersonationGlobalDtoResourceAssembler.toResource(Collections.singletonList(impersonation)))
				.collect(Collectors.toList()));
	}

	@GetMapping("/{name}")
	public ImpersonationByNameDto getByName(@PathVariable String name) {
		Optional<Impersonation> impersonations = impersonationConfiguration.getImpersonation(name);
		if (!impersonations.isPresent()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		Impersonation impersonationOptional = impersonations.orElse(null);
		return impersonationByNameDtoResourceAssembler.toResource(Collections.singletonList(impersonationOptional));
	}

	@PostMapping("")
	public ResponseEntity<ImpersonationByNameDto> postAllImpersonations(@Valid @RequestBody ImpersonationDto impersonation) {
		getNullProperties.getNullProperties(impersonation);
		try {
			impersonationConfiguration.insertImpersonation(impersonation.convertToEntity());
			List<Impersonation> impersonationList = java.util.Arrays.asList(impersonation.convertToEntity());
			return ResponseEntity.ok(impersonationByNameDtoResourceAssembler.toResource(impersonationList));
		} catch (ImpersonationAlreadyExistsException e) {
			e.printStackTrace();
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,
					"Impersonation " + impersonation.getName() + " already exists");
		}
	}
	@PutMapping("")
	public HalMultipleEmbeddedResource<ImpersonationDto> putAllConnections(@Valid @RequestBody List<ImpersonationDto> impersonationDtos) {
		HalMultipleEmbeddedResource<ImpersonationDto> halMultipleEmbeddedResource = new HalMultipleEmbeddedResource<>();
		getListNullProperties.getNullImpersonation(impersonationDtos);
		for (ImpersonationDto impersonationDto : impersonationDtos) {
			try {
				impersonationConfiguration.updateImpersonation(impersonationDto.convertToEntity());
				halMultipleEmbeddedResource.embedResource(impersonationDto);
				halMultipleEmbeddedResource.add(linkTo(methodOn(ImpersonationController.class)
						.getByName(impersonationDto.getName()))
						.withRel(impersonationDto.getName()));
			} catch (ImpersonationDoesNotExistException e) {
				e.printStackTrace();
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
			}
		}

		return halMultipleEmbeddedResource;
	}

	@PutMapping("/{name}")
	public ImpersonationByNameDto putImpersonations(@PathVariable String name,
													@RequestBody ImpersonationDto impersonation) {
//			getNullProperties.getNullProperties(impersonation);
		if (!impersonation.getName().equals(name)) {
			throw new DataNotFoundException(name);
		}
		try {
			impersonationConfiguration.updateImpersonation(impersonation.convertToEntity());
			List<Impersonation> impersonationList = java.util.Arrays.asList(impersonation.convertToEntity());
			return impersonationByNameDtoResourceAssembler.toResource(impersonationList);
		} catch (ImpersonationDoesNotExistException e) {
			e.printStackTrace();
			return null;
		}

	}

	@DeleteMapping("")
	public ResponseEntity<?> deleteAllImpersonation() {
		List<Impersonation> impersonation = impersonationConfiguration.getAllImpersonations();
		if (!impersonation.isEmpty()) {
			impersonationConfiguration.deleteAllImpersonations();
			return ResponseEntity.status(HttpStatus.OK).build();
		}
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@DeleteMapping("/{name}")
	public ResponseEntity<?> deleteByNameImpersonation(@PathVariable String name) {
		Optional<Impersonation> impersonation = impersonationConfiguration.getImpersonation(name);
		if (impersonation.isPresent()) {
			impersonationConfiguration.deleteImpersonation(name);
			return ResponseEntity.status(HttpStatus.OK).build();
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}

}