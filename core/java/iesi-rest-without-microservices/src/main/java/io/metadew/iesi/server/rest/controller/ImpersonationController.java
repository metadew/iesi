package io.metadew.iesi.server.rest.controller;


import io.metadew.iesi.metadata.configuration.ImpersonationConfiguration;
import io.metadew.iesi.metadata.configuration.exception.ImpersonationAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.ImpersonationDoesNotExistException;
import io.metadew.iesi.metadata.definition.Impersonation;
import io.metadew.iesi.server.rest.error.DataBadRequestException;
import io.metadew.iesi.server.rest.error.DataNotFoundException;
import io.metadew.iesi.server.rest.error.GetListNullProperties;
import io.metadew.iesi.server.rest.error.GetNullProperties;
import io.metadew.iesi.server.rest.pagination.ImpersonationCriteria;
import io.metadew.iesi.server.rest.pagination.ImpersonationPagination;
import io.metadew.iesi.server.rest.resource.HalMultipleEmbeddedResource;
import io.metadew.iesi.server.rest.resource.impersonation.dto.ImpersonationDto;
import io.metadew.iesi.server.rest.resource.impersonation.resource.ImpersonatonDtoResourceAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
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
	private final ImpersonationPagination impersonationPagination;

	@Autowired
	ImpersonationController(ImpersonationConfiguration impersonationConfiguration, ImpersonationPagination impersonationPagination, GetNullProperties getNullProperties,
							GetListNullProperties getListNullProperties			) {
		this.impersonationConfiguration = impersonationConfiguration;
		this.getListNullProperties = getListNullProperties;
		this.getNullProperties = getNullProperties;
		this.impersonationPagination = impersonationPagination;
	}
	@Autowired
	private ImpersonatonDtoResourceAssembler impersonatonDtoResourceAssembler ;


	@GetMapping("")
	public HalMultipleEmbeddedResource<ImpersonationDto> getAllImpersonations(@Valid ImpersonationCriteria impersonationCriteria) {
		List<Impersonation> impersonations = impersonationConfiguration.getAllImpersonations();
		List<Impersonation> pagination = impersonationPagination.search(impersonations, impersonationCriteria);
		return new HalMultipleEmbeddedResource<ImpersonationDto>(pagination.stream()
				.filter(distinctByKey(Impersonation::getName))
				.map(impersonation -> impersonatonDtoResourceAssembler.toResource(impersonation))
				.collect(Collectors.toList()));
	}

	@GetMapping("/{name}")
	public ImpersonationDto getByName(@PathVariable String name) {

		return impersonationConfiguration.getImpersonation(name)
				.map(impersonation -> impersonatonDtoResourceAssembler.toResource(impersonation))
				.orElseThrow(() -> new DataNotFoundException(name));
	}

	@PostMapping("")
	public ResponseEntity<ImpersonationDto> postAllImpersonations(@Valid @RequestBody ImpersonationDto impersonationDto) {
		getNullProperties.getNullImpersonation(impersonationDto);
		try {
			impersonationConfiguration.insertImpersonation(impersonationDto.convertToEntity());
			return ResponseEntity.ok(impersonatonDtoResourceAssembler.toResource(impersonationDto.convertToEntity()));
		} catch (ImpersonationAlreadyExistsException e) {
			e.printStackTrace();
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,
					"Impersonation " + impersonationDto.getName() + " already exists");
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
	public ImpersonationDto putImpersonations(@PathVariable String name,
											  @RequestBody ImpersonationDto impersonation) {
 		getNullProperties.getNullImpersonation(impersonation);
		if (!impersonation.getName().equals(name)) {
			throw new DataNotFoundException(name);
		} else if (impersonation.getName() == null) {
			throw new DataBadRequestException(name);
		}
		try {
			impersonationConfiguration.updateImpersonation(impersonation.convertToEntity());
			return impersonatonDtoResourceAssembler.toResource(impersonation.convertToEntity());
		} catch (ImpersonationDoesNotExistException e) {
			e.printStackTrace();
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
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