package io.metadew.iesi.server.rest.connectiontypes.parameter;

import io.metadew.iesi.metadata.definition.connection.ConnectionTypeParameter;

public interface IConnectionTypeParameterDtoService {

    public ConnectionTypeParameterDto convertToDto(ConnectionTypeParameter connectionTypeParameter, String name);

}
