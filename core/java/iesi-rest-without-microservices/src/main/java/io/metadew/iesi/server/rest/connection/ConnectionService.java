package io.metadew.iesi.server.rest.connection;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.metadata.configuration.connection.ConnectionConfiguration;
import io.metadew.iesi.metadata.configuration.environment.EnvironmentConfiguration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.type.ConnectionTypeConfiguration;
import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;
import io.metadew.iesi.metadata.operation.DataObjectOperation;
import io.metadew.iesi.server.rest.connection.dto.ConnectionDto;
import io.metadew.iesi.server.rest.connection.dto.IConnectionDtoService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@ConditionalOnWebApplication
@Log4j2
public class ConnectionService implements IConnectionService {

    private final ConnectionConfiguration connectionConfiguration;
    private final IConnectionDtoService connectionDtoService;
    private final ObjectMapper objectMapper;
    private final EnvironmentConfiguration environmentConfiguration;

    @Autowired
    public ConnectionService(ConnectionConfiguration connectionConfiguration, IConnectionDtoService connectionDtoService, ObjectMapper objectMapper, ConnectionTypeConfiguration connectionTypeConfiguration, EnvironmentConfiguration environmentConfiguration) {
        this.connectionConfiguration = connectionConfiguration;
        this.connectionDtoService = connectionDtoService;
        this.objectMapper = objectMapper;
        this.environmentConfiguration = environmentConfiguration;
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

    @Override
    public List<Connection> importConnections(String textPlain) {
        DataObjectOperation dataObjectOperation = new DataObjectOperation(textPlain);

        return dataObjectOperation.getDataObjects().stream().map((dataObject -> {
            Connection connection = (Connection) objectMapper.convertValue(dataObject, Metadata.class);

            if (!environmentConfiguration.exists(connection.getMetadataKey().getEnvironmentKey())) {
                throw new RuntimeException(String.format("Environment %s does not exist", connection.getMetadataKey().getEnvironmentKey().getName()));
            }

            List<Connection> connections = connectionConfiguration.getByName(connection.getMetadataKey().getName());

            Optional<Connection> connection1 = connections.stream()
                    .filter(connection2 -> connection2.getMetadataKey().getEnvironmentKey().getName().equals(connection.getMetadataKey().getEnvironmentKey().getName()))
                    .findFirst();

            if (connection1.isPresent()) {
                log.info(String.format("Connection %s with environment %s already exists in connection repository, updating to new definition", connection.getMetadataKey().getName(), connection.getMetadataKey().getEnvironmentKey().getName()));
                connectionConfiguration.update(connection);
            } else {
                connectionConfiguration.insert(connection);
            }

            return connection;
        })).collect(Collectors.toList());
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
