package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.ConnectionType;
import io.metadew.iesi.metadata.definition.ConnectionTypeParameter;

public class ConnectionTypeParameterConfiguration {

    private ConnectionTypeParameter connectionTypeParameter;
    private FrameworkExecution frameworkExecution;

    // Constructors
    public ConnectionTypeParameterConfiguration(ConnectionTypeParameter connectionTypeParameter, FrameworkExecution frameworkExecution) {
        this.setConnectionTypeParameter(connectionTypeParameter);
        this.setFrameworkExecution(frameworkExecution);
    }

    public ConnectionTypeParameterConfiguration(FrameworkExecution frameworkExecution) {
        this.setFrameworkExecution(frameworkExecution);
    }

    public ConnectionTypeParameter getConnectionTypeParameter(String connectionTypeName, String connectionTypeParameterName) {
        ConnectionTypeParameter connectionTypeParameterResult = null;
        ConnectionTypeConfiguration connectionTypeConfiguration = new ConnectionTypeConfiguration(this.getFrameworkExecution());
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

    public FrameworkExecution getFrameworkExecution() {
        return frameworkExecution;
    }

    public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
        this.frameworkExecution = frameworkExecution;
    }

}