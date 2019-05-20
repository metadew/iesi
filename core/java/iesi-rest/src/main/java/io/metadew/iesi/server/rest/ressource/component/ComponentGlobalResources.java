package io.metadew.iesi.server.rest.ressource.component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import java.util.List;

import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.metadew.iesi.metadata.definition.Component;
import io.metadew.iesi.server.rest.controller.ComponentsController;
import io.metadew.iesi.server.rest.controller.JsonTransformation.ComponentGlobal;

public class ComponentGlobalResources extends ResourceSupport {
	@JsonProperty(value="result")
	private final List<ComponentGlobal> component;
	
	public ComponentGlobalResources(final List<ComponentGlobal> component) {
		this.component = (List<ComponentGlobal>) component;
			add(linkTo(ComponentsController.class).withSelfRel());
	        add(linkTo(ComponentsController.class).withRel("environment"));
			
		}
}

