package io.metadew.iesi.server.rest.connectiontypes;

import io.metadew.iesi.metadata.definition.connection.ConnectionType;

public interface IConnectionTypeDtoService {

    public ConnectionTypeDto convertToDto(ConnectionType actionType, String name);

}
