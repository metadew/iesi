package io.metadew.iesi.server.rest.connection;

import io.metadew.iesi.metadata.configuration.connection.ConnectionConfiguration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;
import io.metadew.iesi.server.rest.connection.dto.ConnectionDto;
import io.metadew.iesi.server.rest.connection.dto.IConnectionDtoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@ConditionalOnWebApplication
public class ConnectionService implements IConnectionService {

    private final ConnectionConfiguration connectionConfiguration;
    private final IConnectionDtoService connectionDtoService;

    @Autowired
    public ConnectionService(ConnectionConfiguration connectionConfiguration, IConnectionDtoService connectionDtoService) {
        this.connectionConfiguration = connectionConfiguration;
        this.connectionDtoService = connectionDtoService;
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

    public void createConnection(ConnectionDto connectionDto) {
        if (connectionConfiguration.exists(connectionDto.getName())) {
            throw new MetadataAlreadyExistsException(new ConnectionKey(connectionDto.getName(), ""));
        }
        for (Connection connection : connectionDtoService.convertToEntity(connectionDto)) {
            connectionConfiguration.insert(connection);
        }
    }

    public void updateConnection(ConnectionDto connectionDto) {
        for (Connection connection : connectionDtoService.convertToEntity(connectionDto)) {
            connectionConfiguration.update(connection);
        }
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
}
