package io.metadew.iesi.server.rest.controller;

import io.metadew.iesi.metadata.configuration.ComponentConfiguration;
import io.metadew.iesi.metadata.configuration.exception.ComponentAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.ComponentDoesNotExistException;
import io.metadew.iesi.metadata.definition.Component;
import io.metadew.iesi.server.rest.controller.JsonTransformation.ComponentGlobal;
import io.metadew.iesi.server.rest.controller.JsonTransformation.ComponentGlobalByName;
import io.metadew.iesi.server.rest.controller.JsonTransformation.ComponentPost;
import io.metadew.iesi.server.rest.pagination.ComponentCriteria;
import io.metadew.iesi.server.rest.pagination.ComponentRepository;
import io.metadew.iesi.server.rest.ressource.component.ComponentGlobalResources;
import io.metadew.iesi.server.rest.ressource.component.ComponentResource;
import io.metadew.iesi.server.rest.ressource.component.ComponentResources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class ComponentsController {

	private ComponentConfiguration componentConfiguration;

	private final ComponentRepository componentRepository;

	@Autowired
	ComponentsController(ComponentConfiguration componentConfiguration, ComponentRepository componentRepository) {
		this.componentConfiguration = componentConfiguration;
		this.componentRepository = componentRepository;
	}

	@GetMapping("/components")
	public ResponseEntity<ComponentGlobalResources> getAllcomponents(@Valid ComponentCriteria componentCriteria) {
		List<Component> components = componentConfiguration.getComponents();
		List<ComponentGlobal> componentGlobal = components.stream().map(component -> new ComponentGlobal(component))
				.distinct().collect(Collectors.toList());
		List<ComponentGlobal> pagination = componentRepository.search(componentGlobal, componentCriteria);
		final ComponentGlobalResources resource = new ComponentGlobalResources(pagination);
		return ResponseEntity.status(HttpStatus.OK).body(resource);
	}

	@GetMapping("/components/{name}")
	public ResponseEntity<ComponentGlobalByName> getByName(@PathVariable String name) {
		List<Component> component = componentConfiguration.getComponentsByName(name);
		if (!component.isEmpty()) {
			ComponentGlobalByName componentGlobalByNames = new ComponentGlobalByName(component);
			return ResponseEntity.status(HttpStatus.OK).body(componentGlobalByNames);
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

	}

	@GetMapping("/components/{name}/{version}")
	public ResponseEntity<ComponentPost> getComponentsAndVersion(@PathVariable String name,
			@PathVariable Long version) {
		Optional<Component> components = componentConfiguration.getComponent(name, version);
		if (components.isPresent()) {
			Component component = components.orElse(null);
			List<Component> componentlist = java.util.Arrays.asList(component);
			ComponentPost componentPost = new ComponentPost(componentlist);
			return ResponseEntity.status(HttpStatus.OK).body(componentPost);
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}

	@PostMapping("/components")
	public ResponseEntity<ComponentPost> postComponents(@Valid @RequestBody Component component) {
		try {
			componentConfiguration.insertComponent(component);
			List<Component> componentlist = java.util.Arrays.asList(component);
			ComponentPost componentPost = new ComponentPost(componentlist);
			return ResponseEntity.status(HttpStatus.OK).body(componentPost);
		} catch (ComponentAlreadyExistsException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}

	}

	@PutMapping("/components")
	public ResponseEntity<ComponentPost> putComponentsConnection(@Valid @RequestBody List<Component> components)
			throws ComponentDoesNotExistException {
		List<Component> updatedcomponents = new ArrayList<Component>();
		for (Component component : components) {
			componentConfiguration.updateComponent(component);
			Optional.ofNullable(component).ifPresent(updatedcomponents::add);
			ComponentPost componentPost = new ComponentPost(updatedcomponents);
			final ComponentResources resource = new ComponentResources(updatedcomponents);
			return ResponseEntity.status(HttpStatus.OK).body(componentPost);
		}

		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

	}

	@PutMapping("/components/{name}/{version}")
	public ResponseEntity<ComponentResource> putComponentsAndVersion(@Valid @RequestBody Component componentUpdate,
			@PathVariable String name, @PathVariable Long version) {
		
		
		Optional<Component> components = componentConfiguration.getComponent(name, version);
		if (components.isPresent()) {
			Component component = components.orElse(null);
			try {
				componentConfiguration.updateComponent(component);
				final ComponentResource resource = new ComponentResource(componentUpdate, null);
				return ResponseEntity.status(HttpStatus.OK).body(resource);
			} catch (ComponentDoesNotExistException e) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			}
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
	}

	@DeleteMapping("/components")
	public ResponseEntity<?> deleteAllComponents() {
		List<Component> components = componentConfiguration.getComponents();
		if (!components.isEmpty()) {
			componentConfiguration.deleteComponents();
			return ResponseEntity.status(HttpStatus.OK).build();
		}
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@DeleteMapping("/components/{name}")
	public ResponseEntity<?> deleteComponentByName(@PathVariable String name) throws ComponentDoesNotExistException {
		List<Component> components = componentConfiguration.getComponentsByName(name);
		if (!components.isEmpty()) {
			componentConfiguration.deleteComponentByName(name);
			return ResponseEntity.status(HttpStatus.OK).build();
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}

	@DeleteMapping("/components/{name}/{version}")
	public ResponseEntity<?> deleteComponentsAndVersion(@PathVariable String name, @PathVariable Long version)
			throws ComponentDoesNotExistException {
		Optional<Component> components = componentConfiguration.getComponent(name, version);
		if (components.isPresent()) {
			Component component = components.orElse(null);
			componentConfiguration.deleteComponent(component);
			return ResponseEntity.status(HttpStatus.OK).build();
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}
}