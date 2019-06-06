package io.metadew.iesi.server.rest.ressource.connection;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import java.util.List;

import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.metadew.iesi.metadata.definition.Connection;
import io.metadew.iesi.server.rest.controller.ConnectionsController;

public class ConnectionsResources extends ResourceSupport {
	@JsonProperty("result")
	private List<Connection>  connections;

	public ConnectionsResources(List<Connection> connections) {
		this.connections = (List<Connection>) connections;
		add(linkTo(ConnectionsController.class).withSelfRel());
		add(linkTo(ConnectionsController.class).withRel("Connection"));

	}

}