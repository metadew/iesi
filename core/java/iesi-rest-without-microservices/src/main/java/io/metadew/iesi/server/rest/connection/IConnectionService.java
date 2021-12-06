package io.metadew.iesi.server.rest.connection;

import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.server.rest.connection.dto.ConnectionDto;

import java.util.List;

public interface IConnectionService {

    List<Connection> getAll();

    List<Connection> getByName(String name);

    List<Connection> getByEnvironment(String name);

    void createConnection(ConnectionDto connectionDto);

    void updateConnection(ConnectionDto connectionDto);

    void updateConnections(List<ConnectionDto> connectionDtos);

    void deleteAll();
}
