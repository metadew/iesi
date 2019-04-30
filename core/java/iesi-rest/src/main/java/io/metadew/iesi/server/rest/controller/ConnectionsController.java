package io.metadew.iesi.server.rest.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.metadew.iesi.metadata.configuration.ConnectionConfiguration;
import io.metadew.iesi.metadata.configuration.exception.ConnectionAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.ConnectionDoesNotExistException;
import io.metadew.iesi.metadata.definition.Connection;
import io.metadew.iesi.server.rest.controller.JsonTransformation.ConnectionGlobal;
import io.metadew.iesi.server.rest.controller.JsonTransformation.ConnectionName;
import io.metadew.iesi.server.rest.pagination.ConnectionCriteria;
import io.metadew.iesi.server.rest.pagination.ConnectionRepository;
import io.metadew.iesi.server.rest.ressource.connection.ConnectionResource;
import io.metadew.iesi.server.rest.ressource.connection.ConnectionResourceName;
import io.metadew.iesi.server.rest.ressource.connection.ConnectionsGlobal;
import io.metadew.iesi.server.rest.ressource.connection.ConnectionsResources;

@RestController
@RequestMapping("/api")
public class ConnectionsController {

	private static ConnectionConfiguration connectionConfiguration = new ConnectionConfiguration(
			FrameworkConnection.getInstance().getFrameworkExecution());

	private final ConnectionRepository connectionRepository;

	ConnectionsController(ConnectionRepository connectionRepository) {
		this.connectionRepository = connectionRepository;
	}

	@GetMapping("/connections")
	public ResponseEntity<ConnectionsGlobal> getAllConnections(@Valid ConnectionCriteria connectionCriteria) {
		List<Connection> connections = connectionConfiguration.getConnections();
		List<ConnectionGlobal> connectionsGlobal = connections.stream()
				.map(connection -> new ConnectionGlobal(connection)).distinct().collect(Collectors.toList());
		List<ConnectionGlobal> connectionGlobalsFiltered = connectionRepository.search(connectionsGlobal,
				connectionCriteria);
		final ConnectionsGlobal resource = new ConnectionsGlobal(connectionGlobalsFiltered);
		return ResponseEntity.status(HttpStatus.OK).body(resource);
	}

	@GetMapping("/connections/{name}")
	public ResponseEntity<ConnectionResourceName> getByName(@PathVariable String name) {
		List<Connection> connections = connectionConfiguration.getConnectionByName(name);
		if (connections.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		ConnectionName connectionName = new ConnectionName(connections);

		final ConnectionResourceName resource = new ConnectionResourceName(connectionName);
		return ResponseEntity.status(HttpStatus.OK).body(resource);
	}

	@GetMapping("/connections/{name}/{environment}")
	public ResponseEntity<ConnectionResource> getByNameandEnvironment(@PathVariable String name,
			@PathVariable String environment) {
		Optional<Connection> connection = connectionConfiguration.getConnection(name, environment);
		if (connection.isPresent()) {
			return ResponseEntity.status(HttpStatus.OK)
					.body(new ConnectionResource(connection.get(), name, environment));
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}

	@PostMapping("/connections")
	public ResponseEntity<ConnectionResource> postAllConnections(@Valid @RequestBody Connection connection)
			throws ConnectionAlreadyExistsException {
		connectionConfiguration.insertConnection(connection);
		final ConnectionResource resource = new ConnectionResource(connection, null, null);
		return ResponseEntity.status(HttpStatus.OK).body(resource);
	}

	@PutMapping("/connections")
	public ResponseEntity<ConnectionsResources> putAllConnections(@Valid @RequestBody List<Connection> connections)
			throws ConnectionDoesNotExistException {
		List<Connection> updatedConnections = new ArrayList<Connection>();
		for (Connection connection : connections) {
			connectionConfiguration.updateConnection(connection);
			Optional.ofNullable(connection).ifPresent(updatedConnections::add);
		}
		final ConnectionsResources resource = new ConnectionsResources(updatedConnections);
		return ResponseEntity.status(HttpStatus.OK).body(resource);
	}

	@PutMapping("/connections/{name}/{environment}")
	public ResponseEntity<ConnectionResource> putConnections(@PathVariable String name,
			@PathVariable String environment, @RequestBody Connection connection)
			throws ConnectionDoesNotExistException {
		if (!connection.getName().equals(name) || !connection.getEnvironment().equals(environment)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		connectionConfiguration.updateConnection(connection);
		Optional<Connection> updatedConnection = connectionConfiguration.getConnection(name, environment);
		Connection newConnection = updatedConnection.orElse(null);
		final ConnectionResource resource = new ConnectionResource(newConnection, name, environment);
		return ResponseEntity.status(HttpStatus.OK).body(resource);

	}

	@DeleteMapping("/connections")
	public ResponseEntity<?> deleteAllConnections() {
		connectionConfiguration.deleteAllConnections();
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@DeleteMapping("connections/{name}")
	public ResponseEntity<?> deleteConnections(@PathVariable String name) throws ConnectionDoesNotExistException {
		List<Connection> connections = connectionConfiguration.getConnectionByName(name);
		Connection result = connections.stream().filter(x -> x.getName().equals(name)).findAny().orElse(null);
		connectionConfiguration.deleteConnection(result);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@DeleteMapping("/connections/{name}/{environment}")
	public ResponseEntity<?> deleteConnectionsandEnvironment(@PathVariable String name,
			@PathVariable String environment) throws ConnectionDoesNotExistException {
		Optional<Connection> connection = connectionConfiguration.getConnection(name, environment);
		Connection connect = connection.orElse(null);
		if (connection.isPresent()) {
			connectionConfiguration.deleteConnection(connect);
			return ResponseEntity.status(HttpStatus.OK).build();
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}

}