package io.metadew.iesi.server.rest.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.metadew.iesi.metadata.configuration.ComponentConfiguration;
import io.metadew.iesi.metadata.configuration.exception.ComponentAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.ComponentDoesNotExistException;
import io.metadew.iesi.metadata.definition.Component;
import io.metadew.iesi.server.rest.controller.JsonTransformation.ComponentGlobal;
import io.metadew.iesi.server.rest.controller.JsonTransformation.ComponentGlobalByName;
import io.metadew.iesi.server.rest.pagination.ComponentCriteria;
import io.metadew.iesi.server.rest.pagination.ComponentRepository;
import io.metadew.iesi.server.rest.ressource.component.ComponentGlobalResources;
import io.metadew.iesi.server.rest.ressource.component.ComponentResource;
import io.metadew.iesi.server.rest.ressource.component.ComponentResources;

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
		ComponentGlobalByName componentGlobalByNames = new ComponentGlobalByName(component);
		return ResponseEntity.status(HttpStatus.OK).body(componentGlobalByNames);

	}

	@GetMapping("/components/{name}/{version}")
	public ResponseEntity<ComponentResources> getComponentsAndVersion(@PathVariable String name,
			@PathVariable String version) {
		List<Component> components = componentConfiguration.getComponentsByName(name);
		List<Component> result = components.stream()
				.filter(component -> component.getVersion().getDescription().equals(version))
				.collect(Collectors.toList());
		if (!result.isEmpty()) {
			final ComponentResources resource = new ComponentResources(result);
			return ResponseEntity.status(HttpStatus.OK).body(resource);
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}

	@PostMapping("/components")
	public ResponseEntity<ComponentResource> postComponents(@Valid @RequestBody Component component)
			throws ComponentAlreadyExistsException {
		componentConfiguration.insertComponent(component);
		final ComponentResource resource = new ComponentResource(component, null);
		return ResponseEntity.status(HttpStatus.OK).body(resource);
	}

	@PutMapping("/components")
	public ResponseEntity<ComponentResources> putComponentsConnection(@Valid @RequestBody List<Component> components)
			throws ComponentDoesNotExistException {
		List<Component> updatedcomponents = new ArrayList<Component>();
		for (Component component : components) {
			componentConfiguration.updateComponent(component);
			Optional.ofNullable(component).ifPresent(updatedcomponents::add);
		}
		final ComponentResources resource = new ComponentResources(updatedcomponents);
		return ResponseEntity.status(HttpStatus.OK).body(resource);
	}

	@PutMapping("/components/{name}/{version}")
	public ResponseEntity<ComponentResource> putComponentsAndVersion(@Valid @RequestBody Component result,
			@PathVariable String name, @PathVariable String version) throws ComponentDoesNotExistException {
		List<Component> components = componentConfiguration.getComponentsByName(name);
		if (!components.isEmpty()) {
			result = components.stream().filter(component -> component.getVersion().getDescription().equals(version))
					.findAny().orElse(null);

			componentConfiguration.updateComponent(result);
			final ComponentResource resource = new ComponentResource(result, null);
			return ResponseEntity.status(HttpStatus.OK).body(resource);
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}

	@DeleteMapping("/components")
	public ResponseEntity<?> deleteAllComponents() {
		componentConfiguration.deleteComponents();
		return ResponseEntity.status(HttpStatus.OK).build();
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
	public ResponseEntity<?> deleteComponentsAndVersion(@PathVariable String name, @PathVariable String version)
			throws ComponentDoesNotExistException {
		List<Component> components = componentConfiguration.getComponentsByName(name);
		if (!components.isEmpty()) {
			Component result = components.stream()
					.filter(component -> component.getVersion().getDescription().equals(version)).findAny()
					.orElse(null);
			componentConfiguration.deleteComponent(result);
			return ResponseEntity.status(HttpStatus.OK).build();
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}
}