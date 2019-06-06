package io.metadew.iesi.server.rest.controller;


import io.metadew.iesi.metadata.configuration.ImpersonationConfiguration;
import io.metadew.iesi.metadata.configuration.exception.ImpersonationAlreadyExistsException;
import io.metadew.iesi.metadata.definition.Impersonation;
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
//	@PutMapping("")
//	public HalMultipleEmbeddedResource<ImpersonationDto> putAllImpersonations(@Valid @RequestBody List<ImpersonationDto> impersonationDtos) {
//		getListNullProperties.getNullImpersonation(impersonationDtos);
//		HalMultipleEmbeddedResource<ImpersonationDto> halMultipleEmbeddedResource = new HalMultipleEmbeddedResource<>();
//		for (ImpersonationDto impersonationDto : impersonationDtos) {
//			try {
//				ImpersonationDto updatedImpersonationDto = convertToDto(impersonationConfiguration.updateImpersonation(impersonationDto.convertToEntity()));
//				halMultipleEmbeddedResource.embedResource(updatedImpersonationDto);
//				halMultipleEmbeddedResource.add(linkTo(methodOn(ImpersonationController.class)
//						.getByName(updatedImpersonationDto.getName())
//						.withRel(updatedImpersonationDto.getName());
//
//			} catch (ImpersonationDoesNotExistException e) {
//				throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
//						MessageFormat.format("Impersonation {0}-{1} does not exists", impersonationDto.getName(), impersonationDto.getEnvironment()));
//			} catch (ImpersonationAlreadyExistsException e) {
//				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
//			}
//		}
//		return halMultipleEmbeddedResource;
//	}

//	@PutMapping("/{name}")
//	public ImpersonationDto putImpersonations(@PathVariable String name,
//											  @PathVariable String environment, @RequestBody ImpersonationDto impersonationDto) {
//		if (!impersonationDto.getName().equals(name) || !impersonationDto.getEnvironment().equals(environment)) {
//			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
//					MessageFormat.format("Name ''{0}'' and environment ''{1}'' in url do not match name and environment in body",
//							name, environment));
//		}
//		try {
//			return impersonationDtoResourceAssembler.toResource(impersonationConfiguration.updateImpersonation(impersonationDto.convertToEntity()));
//		} catch (ImpersonationDoesNotExistException e) {
//			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
//					MessageFormat.format("Impersonation {0}-{1} does not exist", name, environment));
//		} catch (ImpersonationAlreadyExistsException e) {
//			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//	}

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