package io.metadew.iesi.server.rest.connectiontypes;

import io.metadew.iesi.metadata.definition.connection.ConnectionType;
import io.metadew.iesi.server.rest.connectiontypes.parameter.ConnectionTypeParameterDtoService;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class ConnectionTypeDtoService implements IConnectionTypeDtoService {

    private ConnectionTypeParameterDtoService connectionTypeParameterDtoService;

    public ConnectionTypeDtoService(ConnectionTypeParameterDtoService actionTypeParameterDtoService) {
        this.connectionTypeParameterDtoService = actionTypeParameterDtoService;
    }

    public ConnectionTypeDto convertToDto(ConnectionType connectionType, String name) {
        return new ConnectionTypeDto(name, connectionType.getDescription(),
                connectionType.getParameters().entrySet().stream()
                        .map(entry -> connectionTypeParameterDtoService.convertToDto(entry.getValue(), entry.getKey()))
                        .collect(Collectors.toList()));
    }

}
