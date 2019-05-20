package io.metadew.iesi.server.rest.ressource.component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.metadew.iesi.metadata.definition.Component;
import io.metadew.iesi.server.rest.controller.ComponentsController;
import lombok.Getter;

@Getter
public class ComponentResource extends ResourceSupport{
	@JsonProperty("result")
	private final Component component;
	
	public ComponentResource(final Component component, String name) {
		this.component = component;
			add(linkTo(ComponentsController.class).withSelfRel());
//			add(linkTo(methodOn(ComponentsController.class)
////					.getByName(name))
////					.withSelfRel());
	        add(linkTo(ComponentsController.class).withRel("component"));
			
		}
}