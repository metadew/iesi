package io.metadew.iesi.server.rest.controller;

import io.metadew.iesi.metadata.configuration.ConnectionConfiguration;
import io.metadew.iesi.metadata.configuration.exception.ConnectionAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.ConnectionDoesNotExistException;
import io.metadew.iesi.metadata.definition.Connection;
import io.metadew.iesi.server.rest.pagination.ConnectionCriteria;
import io.metadew.iesi.server.rest.ressource.HalMultipleEmbeddedResource;
import io.metadew.iesi.server.rest.ressource.connection.dto.ConnectionByNameDto;
import io.metadew.iesi.server.rest.ressource.connection.dto.ConnectionDto;
import io.metadew.iesi.server.rest.ressource.connection.dto.ConnectionGlobalDto;
import io.metadew.iesi.server.rest.ressource.connection.resource.ConnectionByNameDtoResourceAssembler;
import io.metadew.iesi.server.rest.ressource.connection.resource.ConnectionDtoResourceAssembler;
import io.metadew.iesi.server.rest.ressource.connection.resource.ConnectionGlobalDtoResourceAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.metadew.iesi.server.rest.helper.Filter.distinctByKey;
import static io.metadew.iesi.server.rest.ressource.connection.dto.ConnectionDto.convertToDto;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping("/connections")
public class ConnectionsController {

	private ConnectionConfiguration connectionConfiguration;

	@Autowired
	ConnectionsController(ConnectionConfiguration connectionConfiguration) {
		this.connectionConfiguration = connectionConfiguration;
	}

	@Autowired
	private ConnectionDtoResourceAssembler connectionDtoResourceAssembler;

	@Autowired
	private ConnectionByNameDtoResourceAssembler connectionByNameDtoResourceAssembler;

	@Autowired
	private ConnectionGlobalDtoResourceAssembler connectionGlobalDtoResourceAssembler;

	@GetMapping("")
	public HalMultipleEmbeddedResource<ConnectionGlobalDto> getAllConnections(@Valid ConnectionCriteria connectionCriteria) {
		return new HalMultipleEmbeddedResource<>(connectionConfiguration.getConnections().stream()
				.filter(distinctByKey(Connection::getName))
				.map(connection -> connectionGlobalDtoResourceAssembler.toResource(Collections.singletonList(connection)))
				.collect(Collectors.toList()));
	}

	@GetMapping("/{name}")
	public ConnectionByNameDto getByName(@PathVariable String name) {
		List<Connection> connections = connectionConfiguration.getConnectionByName(name);
		if (connections.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}

		return connectionByNameDtoResourceAssembler.toResource(connections);
	}

	@GetMapping("/{name}/{environment}")
	public ConnectionDto getByNameandEnvironment(@PathVariable String name,
																			@PathVariable String environment) {
		Optional<Connection> connection = connectionConfiguration.getConnection(name, environment);
		return connection
				.map(connectionDtoResourceAssembler::toResource)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
	}

	@PostMapping("")
	public ConnectionDto postAllConnections(@Valid @RequestBody ConnectionDto connectionDto) {
		try {
			connectionConfiguration.insertConnection(connectionDto.convertToEntity());
		} catch (ConnectionAlreadyExistsException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					MessageFormat.format("Connection {0}-{1} already exists", connectionDto.getName(), connectionDto.getEnvironment()));
		}
		return connectionConfiguration.getConnection(connectionDto.getName(), connectionDto.getEnvironment())
				.map(connectionDtoResourceAssembler::toResource)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));
	}

	@PutMapping("")
	public HalMultipleEmbeddedResource<ConnectionDto> putAllConnections(@Valid @RequestBody List<ConnectionDto> connectionDtos) {
		HalMultipleEmbeddedResource<ConnectionDto> halMultipleEmbeddedResource = new HalMultipleEmbeddedResource<>();
		for (ConnectionDto connectionDto : connectionDtos) {
			try {
				ConnectionDto updatedConnectionDto = convertToDto(connectionConfiguration.updateConnection(connectionDto.convertToEntity()));
				halMultipleEmbeddedResource.embedResource(updatedConnectionDto);
				halMultipleEmbeddedResource.add(linkTo(methodOn(ConnectionsController.class)
						.getByNameandEnvironment(updatedConnectionDto.getName(), updatedConnectionDto.getEnvironment()))
						.withRel(updatedConnectionDto.getName() + ":" + updatedConnectionDto.getEnvironment()));

			} catch (ConnectionDoesNotExistException e) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
						MessageFormat.format("Connection {0}-{1} does not exists", connectionDto.getName(), connectionDto.getEnvironment()));
			} catch (ConnectionAlreadyExistsException e) {
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		return halMultipleEmbeddedResource;
	}

	@PutMapping("/{name}/{environment}")
	public ConnectionDto putConnections(@PathVariable String name,
																   @PathVariable String environment, @RequestBody ConnectionDto connectionDto) {
		if (!connectionDto.getName().equals(name) || !connectionDto.getEnvironment().equals(environment)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					MessageFormat.format("Name ''{0}'' and environment ''{1}'' in url do not match name and environment in body",
							name, environment));
		}
		try {
			return connectionDtoResourceAssembler.toResource(connectionConfiguration.updateConnection(connectionDto.convertToEntity()));
		} catch (ConnectionDoesNotExistException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					MessageFormat.format("Connection {0}-{1} does not exist", name, environment));
		} catch (ConnectionAlreadyExistsException e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping("")
	public ResponseEntity<?> deleteAllConnections() {
		connectionConfiguration.deleteAllConnections();
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@DeleteMapping("/{name}")
	public ResponseEntity<?> deleteConnections(@PathVariable String name) {
		try {
			connectionConfiguration.deleteConnectionByName(name);
		} catch (ConnectionDoesNotExistException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,
					MessageFormat.format("Connection {0} does not exist", name));
		}
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@DeleteMapping("/{name}/{environment}")
	public ResponseEntity<?> deleteConnectionsandEnvironment(@PathVariable String name,
			@PathVariable String environment) {
		try {
			connectionConfiguration.deleteConnection(name, environment);
		} catch (ConnectionDoesNotExistException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,
					MessageFormat.format("Connection {0}-{1} does not exists", name, environment));
		}
		return ResponseEntity.status(HttpStatus.OK).build();
	}

}