package io.metadew.iesi.server.rest.connectiontypes.actiontypes;

import io.metadew.iesi.metadata.definition.connection.ConnectionTypeParameter;
import org.springframework.stereotype.Service;

@Service
public class ConnectionTypeParameterDtoService implements IConnectionTypeParameterDtoService {

    public ConnectionTypeParameterDto convertToDto(ConnectionTypeParameter connectionTypeParameter, String name) {
        return new ConnectionTypeParameterDto(name, connectionTypeParameter.getDescription(), connectionTypeParameter.getType(),
                connectionTypeParameter.isMandatory(), connectionTypeParameter.isEncrypted());
    }

}
