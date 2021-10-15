package io.metadew.iesi.server.rest.connectiontypes.parameter;

import io.metadew.iesi.metadata.definition.connection.ConnectionTypeParameter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnWebApplication
public class ConnectionTypeParameterDtoService implements IConnectionTypeParameterDtoService {

    public ConnectionTypeParameterDto convertToDto(ConnectionTypeParameter connectionTypeParameter, String name) {
        return new ConnectionTypeParameterDto(name, connectionTypeParameter.getDescription(), connectionTypeParameter.getType(),
                connectionTypeParameter.isMandatory(), connectionTypeParameter.isEncrypted());
    }

}
