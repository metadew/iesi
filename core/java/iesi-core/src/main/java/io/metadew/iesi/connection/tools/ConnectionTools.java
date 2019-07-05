package io.metadew.iesi.connection.tools;

import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.configuration.type.ConnectionTypeConfiguration;
import io.metadew.iesi.metadata.definition.ConnectionType;

public final class ConnectionTools {

    public static ConnectionType getConnectionType(FrameworkExecution frameworkExecution, String connectionTypeName) {
        ConnectionTypeConfiguration connectionTypeConfiguration = new ConnectionTypeConfiguration(frameworkExecution.getFrameworkInstance());
        ConnectionType connectionType = null;

        try {
            connectionType = connectionTypeConfiguration.getConnectionType(connectionTypeName);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        return connectionType;
    }
}