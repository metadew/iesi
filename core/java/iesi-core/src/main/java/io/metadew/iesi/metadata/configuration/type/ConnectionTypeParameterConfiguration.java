package io.metadew.iesi.metadata.configuration.type;

import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.connection.ConnectionType;
import io.metadew.iesi.metadata.definition.connection.ConnectionTypeParameter;

public class ConnectionTypeParameterConfiguration {

    private ConnectionTypeParameter connectionTypeParameter;

    // Constructors
    public ConnectionTypeParameterConfiguration(ConnectionTypeParameter connectionTypeParameter) {
        this.setConnectionTypeParameter(connectionTypeParameter);
    }

    public ConnectionTypeParameterConfiguration() {
    }

    public ConnectionTypeParameter getConnectionTypeParameter(String connectionTypeName, String connectionTypeParameterName) {
        ConnectionTypeParameter connectionTypeParameterResult = null;
        ConnectionTypeConfiguration connectionTypeConfiguration = new ConnectionTypeConfiguration();
        ConnectionType connectionType = connectionTypeConfiguration.getConnectionType(connectionTypeName);
        for (ConnectionTypeParameter connectionTypeParameter : connectionType.getParameters()) {
            if (connectionTypeParameter.getName().equalsIgnoreCase(connectionTypeParameterName)) {
                connectionTypeParameterResult = connectionTypeParameter;
                break;
            }
        }
        return connectionTypeParameterResult;
    }

    // Getters and Setters
    public ConnectionTypeParameter getConnectionTypeParameter() {
        return connectionTypeParameter;
    }

    public void setConnectionTypeParameter(ConnectionTypeParameter connectionTypeParameter) {
        this.connectionTypeParameter = connectionTypeParameter;
    }
}