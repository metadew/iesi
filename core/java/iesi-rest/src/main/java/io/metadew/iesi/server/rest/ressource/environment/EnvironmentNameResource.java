package io.metadew.iesi.server.rest.ressource.environment;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.metadew.iesi.server.rest.controller.ConnectionsController;
import io.metadew.iesi.server.rest.controller.JsonTransformation.EnvironmentName;
import lombok.Getter;

@Getter
public class EnvironmentNameResource extends ResourceSupport {
	@JsonProperty("result")
	private EnvironmentName environmentName;

	public EnvironmentNameResource (EnvironmentName environmentName) {
		this.environmentName = environmentName;
		add(linkTo(ConnectionsController.class).withSelfRel());
		add(linkTo(ConnectionsController.class).withRel("Connection"));

	}

}