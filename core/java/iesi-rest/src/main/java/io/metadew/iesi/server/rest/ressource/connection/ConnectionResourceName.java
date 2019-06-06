package io.metadew.iesi.server.rest.ressource.connection;


import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.metadew.iesi.server.rest.controller.ConnectionsController;
import io.metadew.iesi.server.rest.controller.JsonTransformation.ConnectionName;
import lombok.Getter;

@Getter
public class ConnectionResourceName extends ResourceSupport {
	@JsonProperty("result")
	private final ConnectionName connectionName;

	public ConnectionResourceName(final ConnectionName connectionName) {
		this.connectionName = connectionName;
		add(linkTo(ConnectionsController.class).withSelfRel());
		add(linkTo(ConnectionsController.class).withRel("Connection"));

	}

}