package io.metadew.iesi.server.rest.connection;

import io.metadew.iesi.metadata.configuration.connection.ConnectionConfiguration;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;
import io.metadew.iesi.server.rest.connection.dto.ConnectionDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ConnectionService implements IConnectionService {

    private ConnectionConfiguration connectionConfiguration;

    @Autowired
    public ConnectionService(ConnectionConfiguration connectionConfiguration) {
        this.connectionConfiguration = connectionConfiguration;
    }

    public List<Connection> getAll() {
        return connectionConfiguration.getAll();
    }

    public List<Connection> getByName(String name) {
        return connectionConfiguration.getByName(name);
    }

    @Override
    public List<Connection> getByEnvironment(String name) {
        return connectionConfiguration.getByEnvironment(name);
    }

    public Optional<Connection> getByNameAndEnvironment(String name, String environment) {
        return connectionConfiguration.get(new ConnectionKey(name, environment));
    }

    public void createConnection(ConnectionDto connectionDto) {
        connectionConfiguration.insert(connectionDto.convertToEntity());
    }

    public void updateConnection(ConnectionDto connectionDto) {
        connectionConfiguration.update(connectionDto.convertToEntity());
    }

    public void updateConnections(List<ConnectionDto> connectionDtos) {
        connectionDtos.forEach(this::updateConnection);
    }

    public void deleteAll() {
        connectionConfiguration.deleteAll();
    }

    public void deleteByName(String name) {
        connectionConfiguration.deleteByName(name);
    }

    public void deleteByNameAndEnvironment(String name, String environment) {
        connectionConfiguration.delete(new ConnectionKey(name, environment));
    }

}
