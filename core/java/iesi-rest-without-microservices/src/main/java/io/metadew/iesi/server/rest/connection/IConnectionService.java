package io.metadew.iesi.server.rest.connection;

import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.server.rest.connection.dto.ConnectionDto;

import java.util.List;
import java.util.Optional;

public interface IConnectionService {

    public List<Connection> getAll();

    public List<Connection> getByName(String name);

    public List<Connection> getByEnvironment(String name);

    public Optional<Connection> getByNameAndEnvironment(String name, String environment);

    public void createConnection(ConnectionDto connectionDto);

    public void updateConnection(ConnectionDto connectionDto);

    public void updateConnections(List<ConnectionDto> connectionDtos);

    public void deleteAll();

    public void deleteByName(String name);

    public void deleteByNameAndEnvironment(String name, String environment);

}
