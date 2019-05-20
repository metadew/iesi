package io.metadew.iesi.server.rest.ressource.environment;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import java.security.Principal;

import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.metadew.iesi.metadata.definition.Environment;
import io.metadew.iesi.server.rest.controller.ConnectionsController;
import io.metadew.iesi.server.rest.controller.EnvironmentsController;
import lombok.Getter;

@Getter
public class EnvironmentResource extends ResourceSupport {
	@JsonProperty("result")
	private final Environment environment;

	public EnvironmentResource(final Environment environment, String name) {
		this.environment = environment;
		add(linkTo(EnvironmentsController.class).withSelfRel());
//		add(linkTo(methodOn(EnvironmentsController.class)
//				.getByName(name))
//				.withSelfRel());
		add(linkTo(EnvironmentsController.class).withRel("environment"));
//		add(linkTo(ConnectionsController.class).withRel("Name of user" + " " + principal.getName()));

	}

}