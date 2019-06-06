//package io.metadew.iesi.server.rest.controller;
//
//
//import io.metadew.iesi.metadata.configuration.ComponentConfiguration;
//import io.metadew.iesi.metadata.configuration.exception.ComponentAlreadyExistsException;
//import io.metadew.iesi.metadata.configuration.exception.ComponentDoesNotExistException;
//import io.metadew.iesi.metadata.definition.Component;
//import io.metadew.iesi.server.rest.pagination.ComponentCriteria;
//import io.metadew.iesi.server.rest.ressource.HalMultipleEmbeddedResource;
//import io.metadew.iesi.server.rest.ressource.component.dto.ComponentByNameDto;
//import io.metadew.iesi.server.rest.ressource.component.dto.ComponentDto;
//import io.metadew.iesi.server.rest.ressource.component.dto.ComponentGlobalDto;
//import io.metadew.iesi.server.rest.ressource.component.resource.ComponentByNameDtoResourceAssembler;
//import io.metadew.iesi.server.rest.ressource.component.resource.ComponentDtoResourceAssembler;
//import io.metadew.iesi.server.rest.ressource.component.resource.ComponentGlobalDtoResourceAssembler;
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
//import static io.metadew.iesi.server.rest.ressource.component.dto.ComponentDto.convertToDto;
//import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
//import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
//
//@RestController
//@RequestMapping("/components")
//public class ComponentsController {
//
//	private ComponentConfiguration componentConfiguration;
//
//	@Autowired
//	ComponentsController(ComponentConfiguration componentConfiguration) {
//		this.componentConfiguration = componentConfiguration;
//	}
//
//	@Autowired
//	private ComponentDtoResourceAssembler componentDtoResourceAssembler;
//
//	@Autowired
//	private ComponentByNameDtoResourceAssembler componentByNameDtoResourceAssembler;
//
//	@Autowired
//	private ComponentGlobalDtoResourceAssembler componentGlobalDtoResourceAssembler;
//
//	@GetMapping("")
//	public HalMultipleEmbeddedResource<ComponentGlobalDto> getAllComponents(@Valid ComponentCriteria componentCriteria) {
//		return new HalMultipleEmbeddedResource<>(componentConfiguration.getComponents().stream()
//				.filter(distinctByKey(Component::getName))
//				.map(component -> componentGlobalDtoResourceAssembler.toResource(Collections.singletonList(component)))
//				.collect(Collectors.toList()));
//	}
//
//	@GetMapping("/{name}")
//	public ComponentByNameDto getByName(@PathVariable String name) {
//		List<Component> components = componentConfiguration.getComponentByName(name);
//		if (components.isEmpty()) {
//			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
//		}
//
//		return componentByNameDtoResourceAssembler.toResource(components);
//	}
//
//	@GetMapping("/{name}/{version}")
//	public ComponentDto getByNameandEnvironment(@PathVariable String name,
//												@PathVariable Long version) {
//		Optional<Component> component = componentConfiguration.getComponent(name, version);
//		return component
//				.map(componentDtoResourceAssembler::toResource)
//				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
//	}
//
//	@PostMapping("")
//	public ComponentDto postAllComponents(@Valid @RequestBody ComponentDto componentDto) {
//		try {
//			componentConfiguration.insertComponent(componentDto.convertToEntity());
//		} catch (ComponentAlreadyExistsException e) {
//			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
//					MessageFormat.format("Component {0}-{1} already exists", componentDto.getName(), componentDto.getEnvironment()));
//		}
//		return componentConfiguration.getComponent(componentDto.getName(), componentDto.getEnvironment())
//				.map(componentDtoResourceAssembler::toResource)
//				.orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));
//	}
//
//	@PutMapping("")
//	public HalMultipleEmbeddedResource<ComponentDto> putAllComponents(@Valid @RequestBody List<ComponentDto> componentDtos) {
//		HalMultipleEmbeddedResource<ComponentDto> halMultipleEmbeddedResource = new HalMultipleEmbeddedResource<>();
//		for (ComponentDto componentDto : componentDtos) {
//			try {
//				ComponentDto updatedComponentDto = convertToDto(componentConfiguration.updateComponent(componentDto.convertToEntity()));
//				halMultipleEmbeddedResource.embedResource(updatedComponentDto);
//				halMultipleEmbeddedResource.add(linkTo(methodOn(ComponentsController.class)
//						.getByNameandEnvironment(updatedComponentDto.getName(), updatedComponentDto.getEnvironment()))
//						.withRel(updatedComponentDto.getName() + ":" + updatedComponentDto.getEnvironment()));
//
//			} catch (ComponentDoesNotExistException e) {
//				throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
//						MessageFormat.format("Component {0}-{1} does not exists", componentDto.getName(), componentDto.getEnvironment()));
//			} catch (ComponentAlreadyExistsException e) {
//				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
//			}
//		}
//		return halMultipleEmbeddedResource;
//	}
//
//	@PutMapping("/{name}/{version}")
//	public ComponentDto putComponents(@PathVariable String name,
//									  @PathVariable Long version, @RequestBody ComponentDto componentDto) {
//		if (!componentDto.getName().equals(name) || !componentDto.getEnvironment().equals(version)) {
//			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
//					MessageFormat.format("Name ''{0}'' and version ''{1}'' in url do not match name and version in body",
//							name, version));
//		}
//		try {
//			return componentDtoResourceAssembler.toResource(componentConfiguration.updateComponent(componentDto.convertToEntity()));
//		} catch (ComponentDoesNotExistException e) {
//			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
//					MessageFormat.format("Component {0}-{1} does not exist", name, version));
//		} catch (ComponentAlreadyExistsException e) {
//			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//	}
//
//
//	@DeleteMapping("/components")
//	public ResponseEntity<?> deleteAllComponents() {
//		List<Component> components = componentConfiguration.getComponents();
//		if (!components.isEmpty()) {
//			componentConfiguration.deleteComponents();
//			return ResponseEntity.status(HttpStatus.OK).build();
//		}
//		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
//	}
//
//	@DeleteMapping("/components/{name}")
//	public ResponseEntity<?> deleteComponentByName(@PathVariable String name) throws ComponentDoesNotExistException {
//		List<Component> components = componentConfiguration.getComponentsByName(name);
//		if (!components.isEmpty()) {
//			componentConfiguration.deleteComponentByName(name);
//			return ResponseEntity.status(HttpStatus.OK).build();
//		}
//		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//	}
//
//	@DeleteMapping("/components/{name}/{version}")
//	public ResponseEntity<?> deleteComponentsAndVersion(@PathVariable String name, @PathVariable Long version)
//			throws ComponentDoesNotExistException {
//		Optional<Component> components = componentConfiguration.getComponent(name, version);
//		if (components.isPresent()) {
//			Component component = components.orElse(null);
//			componentConfiguration.deleteComponent(component);
//			return ResponseEntity.status(HttpStatus.OK).build();
//		}
//		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//	}
//}