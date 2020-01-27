package io.metadew.iesi.server.rest.controller;

import io.metadew.iesi.metadata.configuration.connection.ConnectionConfiguration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;
import io.metadew.iesi.server.rest.error.DataBadRequestException;
import io.metadew.iesi.server.rest.error.DataNotFoundException;
import io.metadew.iesi.server.rest.pagination.ConnectionCriteria;
import io.metadew.iesi.server.rest.pagination.ConnectionPagination;
import io.metadew.iesi.server.rest.resource.HalMultipleEmbeddedResource;
import io.metadew.iesi.server.rest.resource.connection.dto.ConnectionByNameDto;
import io.metadew.iesi.server.rest.resource.connection.dto.ConnectionDto;
import io.metadew.iesi.server.rest.resource.connection.dto.ConnectionGlobalDto;
import io.metadew.iesi.server.rest.resource.connection.resource.ConnectionByNameDtoResourceAssembler;
import io.metadew.iesi.server.rest.resource.connection.resource.ConnectionDtoResourceAssembler;
import io.metadew.iesi.server.rest.resource.connection.resource.ConnectionGlobalDtoResourceAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.metadew.iesi.server.rest.helper.Filter.distinctByKey;

@RestController
@RequestMapping("/connections")
public class ConnectionsController {

	private ConnectionConfiguration connectionConfiguration;
	private ConnectionPagination connectionPagination;
	private ConnectionDtoResourceAssembler connectionDtoResourceAssembler;
	private ConnectionByNameDtoResourceAssembler connectionByNameDtoResourceAssembler;
	private ConnectionGlobalDtoResourceAssembler connectionGlobalDtoResourceAssembler;

	@Autowired
	ConnectionsController(ConnectionConfiguration connectionConfiguration, ConnectionPagination connectionPagination,
						  ConnectionDtoResourceAssembler connectionDtoResourceAssembler, ConnectionByNameDtoResourceAssembler connectionByNameDtoResourceAssembler,
						  ConnectionGlobalDtoResourceAssembler connectionGlobalDtoResourceAssembler) {
		this.connectionConfiguration = connectionConfiguration;
		this.connectionPagination = connectionPagination;
		this.connectionDtoResourceAssembler = connectionDtoResourceAssembler;
		this.connectionByNameDtoResourceAssembler = connectionByNameDtoResourceAssembler;
		this.connectionGlobalDtoResourceAssembler = connectionGlobalDtoResourceAssembler;
	}


	@GetMapping("")
	public HalMultipleEmbeddedResource<ConnectionGlobalDto> getAll(@Valid ConnectionCriteria connectionCriteria) {
		List<Connection> connections = connectionConfiguration.getAll();
		List<Connection> pagination = connectionPagination.search(connections, connectionCriteria);
		return new HalMultipleEmbeddedResource<>(pagination.stream()
				.filter(distinctByKey(Connection::getName))
				.map(connection -> connectionGlobalDtoResourceAssembler.toResource(Collections.singletonList(connection)))
				.collect(Collectors.toList()));
	}

	@GetMapping("/{name}")
	public ConnectionByNameDto getByName(@PathVariable String name) {
		List<Connection> connections = connectionConfiguration.getByName(name);
		if (connections.isEmpty()) {
			throw  new DataNotFoundException(name);
		}
		return connectionByNameDtoResourceAssembler.toResource(connections);
	}

	@GetMapping("/{name}/{environment}")
	public ConnectionDto get(@PathVariable String name, @PathVariable String environment) {
		Optional<Connection> connection = connectionConfiguration.get(name, environment);
		return connection
				.map(connectionDtoResourceAssembler::toResource)
				.orElseThrow(() -> new DataNotFoundException(name, environment));
	}

	@PostMapping("")
	public ResponseEntity<ConnectionDto> post(@Valid @RequestBody ConnectionDto connectionDto) {
		try {
			connectionConfiguration.insert(connectionDto.convertToEntity());
			return ResponseEntity.ok(connectionDtoResourceAssembler.toResource(connectionDto.convertToEntity()));
		} catch (MetadataAlreadyExistsException e) {
					e.printStackTrace();
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,
					"Connection " + connectionDto.getName() + " already exists");
		}
	}

	@PutMapping("")
	public HalMultipleEmbeddedResource<ConnectionDto> putAll(@Valid @RequestBody List<ConnectionDto> connectionDtos) {
		HalMultipleEmbeddedResource<ConnectionDto> halMultipleEmbeddedResource = new HalMultipleEmbeddedResource<>();
		for (ConnectionDto connectionDto : connectionDtos) {
			try {
				connectionConfiguration.update(connectionDto.convertToEntity());
				ConnectionDto updatedConnectionDto = connectionDtoResourceAssembler.toResource(connectionDto.convertToEntity());
				halMultipleEmbeddedResource.embedResource(updatedConnectionDto);
			} catch (MetadataDoesNotExistException e) {
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		return halMultipleEmbeddedResource;
	}

	@PutMapping("/{name}/{environment}")
	public ConnectionDto put(@PathVariable String name, @PathVariable String environment, @RequestBody ConnectionDto connectionDto) {
		if (!connectionDto.getName().equals(name) || !connectionDto.getEnvironment().equals(environment)) {
			throw new DataBadRequestException(name);
		} else if (connectionDto.getName() == null){
			throw new DataNotFoundException(name);
		}
		try {
			connectionConfiguration.update(connectionDto.convertToEntity());
			return connectionDtoResourceAssembler.toResource(connectionDto.convertToEntity());
		} catch (MetadataDoesNotExistException e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping("")
	public ResponseEntity<?> deleteAll() {
		List<Connection> connections = connectionConfiguration.getAll();
		if (!connections.isEmpty()) {
			connectionConfiguration.deleteAll();
			return ResponseEntity.status(HttpStatus.OK).build();
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}

	@DeleteMapping("/{name}")
	public ResponseEntity<?> deleteByName(@PathVariable String name) {
		List<Connection> connections = connectionConfiguration.getByName(name);
		if (connections.isEmpty()) {
			throw new DataNotFoundException(name);
		}
		try {
			connectionConfiguration.deleteByName(name);
			return ResponseEntity.status(HttpStatus.OK).build();
		} catch (MetadataDoesNotExistException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

	@DeleteMapping("/{name}/{environment}")
	public ResponseEntity<?> delete(@PathVariable String name, @PathVariable String environment) {
		Optional<Connection> connections = connectionConfiguration.get(name, environment);
		if (!connections.isPresent()) {
			throw new DataNotFoundException(name, environment);
		}
		try {
			connectionConfiguration.delete(new ConnectionKey(name, environment));
			return ResponseEntity.status(HttpStatus.OK).build();
		} catch (MetadataDoesNotExistException e) {
			throw new DataNotFoundException(name, environment);
		}

	}

}