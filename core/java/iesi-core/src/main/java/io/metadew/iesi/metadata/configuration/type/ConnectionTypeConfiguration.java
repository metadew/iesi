package io.metadew.iesi.metadata.configuration.type;

import io.metadew.iesi.common.configuration.metadata.connectiontypes.MetadataConnectionTypesConfiguration;
import io.metadew.iesi.metadata.definition.connection.ConnectionType;
import org.springframework.stereotype.Component;

@Component
public class ConnectionTypeConfiguration {

    private ConnectionType connectionType;
    private String dataObjectType = "ConnectionType";

    private final MetadataConnectionTypesConfiguration metadataConnectionTypesConfiguration;

    public ConnectionTypeConfiguration(MetadataConnectionTypesConfiguration metadataConnectionTypesConfiguration) {
        this.metadataConnectionTypesConfiguration = metadataConnectionTypesConfiguration;
    }

    public ConnectionType getConnectionType(String connectionTypeName) {
        return metadataConnectionTypesConfiguration.getConnectionType(connectionTypeName)
                .orElseThrow(() -> new RuntimeException("connection type " + connectionTypeName + " not found"));
    }

    public void setConnectionType(ConnectionType connectionType) {
        this.connectionType = connectionType;
    }

}