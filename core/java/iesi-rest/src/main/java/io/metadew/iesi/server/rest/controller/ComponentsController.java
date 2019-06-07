package io.metadew.iesi.server.rest.controller;

import io.metadew.iesi.metadata.configuration.ComponentConfiguration;
import io.metadew.iesi.metadata.configuration.exception.ComponentAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.ComponentDoesNotExistException;
import io.metadew.iesi.metadata.definition.Component;

import io.metadew.iesi.server.rest.controller.JsonTransformation.ComponentGlobalByName;
import io.metadew.iesi.server.rest.controller.JsonTransformation.ComponentPost;
import io.metadew.iesi.server.rest.error.DataNotFoundException;
import io.metadew.iesi.server.rest.error.GetListNullProperties;
import io.metadew.iesi.server.rest.error.GetNullProperties;
import io.metadew.iesi.server.rest.pagination.ComponentCriteria;
import io.metadew.iesi.server.rest.pagination.ComponentRepository;
import io.metadew.iesi.server.rest.ressource.HalMultipleEmbeddedResource;
import io.metadew.iesi.server.rest.ressource.component.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import static io.metadew.iesi.server.rest.ressource.component.ComponentPostByNameDto.convertToDto;
import static io.metadew.iesi.server.rest.helper.Filter.distinctByKey;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
@RestController
public class ComponentsController {

	private ComponentConfiguration componentConfiguration;

	private final ComponentRepository componentRepository;

	private final GetNullProperties getNullProperties;

	private final GetListNullProperties getListNullProperties;

	@Autowired
	private ComponentGetByNameGetDtoAssembler componentGetByNameGetDtoAssembler;


	@Autowired
	private ComponentGlobalDtoResourceAssembler componentGlobalDtoResourceAssembler;

	@Autowired
	private ComponentPostByNameDtoResourceAssembler componentPostByNameDtoResourceAssembler;

	@Autowired
	ComponentsController(GetNullProperties getNullProperties,GetListNullProperties getListNullProperties,ComponentConfiguration componentConfiguration,ComponentGlobalDtoResourceAssembler componentGlobalDtoResourceAssembler, ComponentRepository componentRepository) {
		this.componentConfiguration = componentConfiguration;
		this.componentRepository = componentRepository;
		this.getListNullProperties = getListNullProperties;
		this.getNullProperties = getNullProperties;
		this.componentGlobalDtoResourceAssembler = componentGlobalDtoResourceAssembler;
	}


	@GetMapping("/components")
	public HalMultipleEmbeddedResource<ComponentGlobalDto> getAllComponents(@Valid ComponentCriteria componentCriteria){
		return new HalMultipleEmbeddedResource<>(componentConfiguration.getComponents().stream()
				.filter(distinctByKey(Component :: getName))
				.map(component -> componentGlobalDtoResourceAssembler.toResource(Collections.singletonList(component)))
				.collect(Collectors.toList()));
	}


	@GetMapping("/components/{name}")
	public ResponseEntity<ComponentGetByNameDto> getByName(@PathVariable String name) {
		List<Component> component = componentConfiguration.getComponentsByName(name);
		if (component.isEmpty()){
			throw new DataNotFoundException(name);
		}
		return ResponseEntity.ok(componentGetByNameGetDtoAssembler.toResource(component));
	}

	@GetMapping("/components/{name}/{version}")
	public ResponseEntity<ComponentPostByNameDto> getComponentsAndVersion(@PathVariable String name,
			@PathVariable Long version) {
		Optional<Component> components = componentConfiguration.getComponent(name, version);
		if (!components.isPresent()) {
			throw new DataNotFoundException(name, version);
		}
		Component component = components.orElse(null);
		List<Component> componentlist = java.util.Arrays.asList(component);
		ComponentPost componentPost = new ComponentPost(componentlist);
		return ResponseEntity.ok(componentPostByNameDtoResourceAssembler.toResource(componentlist));
	}
//
	@PostMapping("/components")
	public ResponseEntity<ComponentPostByNameDto> postComponents(@Valid @RequestBody ComponentPostByNameDto component) {
		try {
			componentConfiguration.insertComponent(component.convertToEntity());
			List<Component> componentlist = java.util.Arrays.asList(component.convertToEntity());
			return ResponseEntity.ok(componentPostByNameDtoResourceAssembler.toResource(componentlist));
		} catch (ComponentAlreadyExistsException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}

	}

	@PutMapping("/components")
	public HalMultipleEmbeddedResource<ComponentPostByNameDto> putAllConnections(@Valid @RequestBody List<ComponentPostByNameDto> componentDtos) {
		HalMultipleEmbeddedResource<ComponentPostByNameDto> halMultipleEmbeddedResource = new HalMultipleEmbeddedResource<>();
		getListNullProperties.getNullComponent(componentDtos);
		for (ComponentPostByNameDto componentDto : componentDtos) {
			try {
				componentConfiguration.updateComponent(componentDto.convertToEntity());
				Component component = componentDto.convertToEntity();
				List<Component> componentList = java.util.Arrays.asList(component);
				ComponentPostByNameDto componentByNameDto = convertToDto(componentList);
				halMultipleEmbeddedResource.embedResource(componentByNameDto);
				halMultipleEmbeddedResource.add(linkTo(methodOn(ComponentsController.class)
						.getByName(componentDto.getName()))
						.withRel(componentDto.getName()));
			} catch (ComponentDoesNotExistException e) {
				e.printStackTrace();
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
			}
		}

		return halMultipleEmbeddedResource;
	}


//	@PutMapping("/components/{name}/{version}")
//	public ComponentPostByNameDto putComponents(@PathVariable String name, @PathVariable Long version,
//											@RequestBody ComponentPostByNameDto component) {
//		getNullProperties.getNullProperties(component);
//		Optional<Component> components = componentConfiguration.getComponent(name, version);
//		if (!component.getName().equals(name) || !component.getVersions().equals(version)) {
//			throw new DataNotFoundException(name);
//		}
//		try {
//			componentConfiguration.updateComponent(component.convertToEntity());
//			List<Component> componentList = java.util.Arrays.asList(component.convertToEntity());
//			return componentPostByNameDtoResourceAssembler.toResource(componentList);
//		} catch (ComponentDoesNotExistException e) {
//			e.printStackTrace();
//			return null;
//		}
//
//	}

	@DeleteMapping("/components")
	public ResponseEntity<?> deleteAllComponents() {
		List<Component> components = componentConfiguration.getComponents();
		if (!components.isEmpty()) {
			componentConfiguration.deleteComponents();
			return ResponseEntity.status(HttpStatus.OK).build();
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}

	@DeleteMapping("/components/{name}")
	public ResponseEntity<?> deleteComponentByName(@PathVariable String name) {
		List<Component> components = componentConfiguration.getComponentsByName(name);
		if (components.isEmpty()) {
			throw new DataNotFoundException(name);
		}
		try {
			componentConfiguration.deleteComponentByName(name);
			return ResponseEntity.status(HttpStatus.OK).build();
		} catch (ComponentDoesNotExistException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}

	@DeleteMapping("/components/{name}/{version}")
	public ResponseEntity<?> deleteComponentsAndVersion(@PathVariable String name, @PathVariable Long version) {
		Optional<Component> components = componentConfiguration.getComponent(name, version);
		if (!components.isPresent()) {
			throw new DataNotFoundException(name, version);
		}
		try {
			Component component = components.orElse(null);
			componentConfiguration.deleteComponent(component);
			return ResponseEntity.status(HttpStatus.OK).build();
		} catch (ComponentDoesNotExistException e) {
			e.printStackTrace();
			throw new DataNotFoundException(name, version);

		}
	}
}