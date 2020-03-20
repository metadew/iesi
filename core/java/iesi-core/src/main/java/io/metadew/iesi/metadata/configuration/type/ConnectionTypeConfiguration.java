package io.metadew.iesi.metadata.configuration.type;

import io.metadew.iesi.framework.configuration.metadata.connectiontypes.MetadataConnectionTypesConfiguration;
import io.metadew.iesi.metadata.definition.connection.ConnectionType;

public class ConnectionTypeConfiguration {

    private ConnectionType connectionType;
    private String dataObjectType = "ConnectionType";

    // Constructors
    public ConnectionTypeConfiguration(ConnectionType connectionType) {
        this.setConnectionType(connectionType);
    }

    public ConnectionTypeConfiguration() {
    }

    public ConnectionType getConnectionType(String connectionTypeName) {
        return MetadataConnectionTypesConfiguration.getInstance().getConnectionType(connectionTypeName)
                .orElseThrow(() -> new RuntimeException("connection type " + connectionTypeName + " not found"));
    }

    // Getters and Setters
    public ConnectionType getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(ConnectionType connectionType) {
        this.connectionType = connectionType;
    }

    public String getDataObjectType() {
        return dataObjectType;
    }

    public void setDataObjectType(String dataObjectType) {
        this.dataObjectType = dataObjectType;
    }

}