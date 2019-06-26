package io.metadew.iesi.metadata.configuration.type;

import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.ConnectionType;
import io.metadew.iesi.metadata.definition.ConnectionTypeParameter;

public class ConnectionTypeParameterConfiguration {

    private ConnectionTypeParameter connectionTypeParameter;
    private FrameworkInstance frameworkInstance;

    // Constructors
    public ConnectionTypeParameterConfiguration(ConnectionTypeParameter connectionTypeParameter, FrameworkInstance frameworkInstance) {
        this.setConnectionTypeParameter(connectionTypeParameter);
        this.setFrameworkInstance(frameworkInstance);
    }

    public ConnectionTypeParameterConfiguration(FrameworkInstance frameworkInstance) {
    	this.setFrameworkInstance(frameworkInstance);
    }

    public ConnectionTypeParameter getConnectionTypeParameter(String connectionTypeName, String connectionTypeParameterName) {
        ConnectionTypeParameter connectionTypeParameterResult = null;
        ConnectionTypeConfiguration connectionTypeConfiguration = new ConnectionTypeConfiguration(this.getFrameworkInstance());
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

	public FrameworkInstance getFrameworkInstance() {
		return frameworkInstance;
	}

	public void setFrameworkInstance(FrameworkInstance frameworkInstance) {
		this.frameworkInstance = frameworkInstance;
	}

}