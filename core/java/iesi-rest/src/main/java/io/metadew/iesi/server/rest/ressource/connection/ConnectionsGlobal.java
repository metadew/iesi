package io.metadew.iesi.server.rest.ressource.connection;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import java.util.List;

import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.metadew.iesi.server.rest.controller.ConnectionsController;
import io.metadew.iesi.server.rest.controller.JsonTransformation.ConnectionGlobal;

public class ConnectionsGlobal extends ResourceSupport {
	@JsonProperty("result")
	private final List<ConnectionGlobal> connectionGlobalsFiltered;

	public ConnectionsGlobal(final List<ConnectionGlobal> connectionGlobalsFiltered) {
		this.connectionGlobalsFiltered = (List<ConnectionGlobal>) connectionGlobalsFiltered;
		add(linkTo(ConnectionsController.class).withSelfRel());
		add(linkTo(ConnectionsController.class).withRel("Connection"));

	}
}
