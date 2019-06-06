package io.metadew.iesi.server.rest.ressource.connection;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.metadew.iesi.metadata.definition.Connection;
import io.metadew.iesi.metadata.definition.User;
import io.metadew.iesi.server.rest.controller.ConnectionsController;
import lombok.Getter;

@Getter
public class ConnectionResource extends ResourceSupport {
	@JsonProperty("result")
	private final Connection connection;

	public ConnectionResource(final Connection connection, String name, String environment) {
		User user = new User();
		this.connection = connection;

		add(linkTo(ConnectionsController.class).withSelfRel());
		add(linkTo(ConnectionsController.class).slash(user.getLastName()).withSelfRel());
		add(linkTo(ConnectionsController.class).withRel("Connection"));

	}

}