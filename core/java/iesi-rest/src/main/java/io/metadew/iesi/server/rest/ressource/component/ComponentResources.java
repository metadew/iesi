package io.metadew.iesi.server.rest.ressource.component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import java.util.List;

import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.metadew.iesi.metadata.definition.Component;
import io.metadew.iesi.server.rest.controller.ComponentsController;
import lombok.Getter;

@Getter
public class ComponentResources extends ResourceSupport {
	@JsonProperty(value="result")
	private final List<Component> component;
	
	public ComponentResources(final List<Component> component) {
		this.component = (List<Component>) component;
			add(linkTo(ComponentsController.class).withSelfRel());
	        add(linkTo(ComponentsController.class).withRel("environment"));
			
		}
}

