package io.metadew.iesi.metadata.configuration.type;

import io.metadew.iesi.common.configuration.metadata.connectiontypes.MetadataConnectionTypesConfiguration;
import io.metadew.iesi.metadata.definition.connection.ConnectionType;

public class ConnectionTypeConfiguration {

    private ConnectionType connectionType;
    private String dataObjectType = "ConnectionType";

    public ConnectionType getConnectionType(String connectionTypeName) {
        return MetadataConnectionTypesConfiguration.getInstance().getConnectionType(connectionTypeName)
                .orElseThrow(() -> new RuntimeException("connection type " + connectionTypeName + " not found"));
    }

    public void setConnectionType(ConnectionType connectionType) {
        this.connectionType = connectionType;
    }

}