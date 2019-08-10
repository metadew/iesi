package io.metadew.iesi.connection.tools;

import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.configuration.type.ConnectionTypeConfiguration;
import io.metadew.iesi.metadata.definition.connection.ConnectionType;

public final class ConnectionTools {

    public static ConnectionType getConnectionType(String connectionTypeName) {
        ConnectionTypeConfiguration connectionTypeConfiguration = new ConnectionTypeConfiguration();
        ConnectionType connectionType;

        try {
            connectionType = connectionTypeConfiguration.getConnectionType(connectionTypeName);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        return connectionType;
    }
}