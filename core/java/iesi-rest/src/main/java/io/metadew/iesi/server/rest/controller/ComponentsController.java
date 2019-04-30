package io.metadew.iesi.server.rest.controller;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.metadew.iesi.metadata.configuration.ComponentConfiguration;
import io.metadew.iesi.metadata.definition.Component;

@RestController
@RequestMapping("/api")
public class ComponentsController {

	private static ComponentConfiguration componentConfiguration = new ComponentConfiguration(
			FrameworkConnection.getInstance().getFrameworkExecution());
	
	@GetMapping("/components")
	public Component getAllcomponents() {

		Component component = componentConfiguration.getComponent();
		return component;
	}

	@GetMapping("/components/{name}")
	public Component getByName(@PathVariable String name) {
		Component component = componentConfiguration.getComponent(name);
		return component;
	}

	@GetMapping("/components/{name}/{version}")
	public Component getComponentsAndVersion(@PathVariable Component component) {

		return component;
	}

	@PostMapping("/components")
	public Component postComponents(@Valid @RequestBody Component component) {

		return component;
	}

	@PutMapping("/components/{name}")
	public Component putComponentsConnection(@PathVariable Component component) {

		return component;
	}

	@PutMapping("/components/{name}/{version}")
	public Component putComponentsAndVersion(@PathVariable Component component) {

		return component;
	}

	@DeleteMapping("/components")
	public Component deleteAllComponents(Component component) {

		return component;
	}

	@DeleteMapping("/components/{name}")
	public Component deleteComponents(@PathVariable Component component) {

		return component;
	}

	@DeleteMapping("/components/{name}/{version}")
	public Component deleteComponentsAndVersion(Component component) {

		return component;
	}
}