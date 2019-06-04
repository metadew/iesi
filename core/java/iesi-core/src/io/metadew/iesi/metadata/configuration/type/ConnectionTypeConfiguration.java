package io.metadew.iesi.metadata.configuration.type;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.ConnectionType;
import io.metadew.iesi.metadata.operation.DataObjectOperation;
import io.metadew.iesi.metadata.operation.TypeConfigurationOperation;

public class ConnectionTypeConfiguration {

    private ConnectionType connectionType;
    private FrameworkInstance frameworkInstance;
    private String dataObjectType = "ConnectionType";

    // Constructors
    public ConnectionTypeConfiguration(ConnectionType connectionType, FrameworkInstance frameworkInstance) {
        this.setConnectionType(connectionType);
        this.setFrameworkInstance(frameworkInstance);
    }

    public ConnectionTypeConfiguration(FrameworkInstance frameworkInstance) {
    	this.setFrameworkInstance(frameworkInstance);
    }

    public ConnectionType getConnectionType(String connectionTypeName) {
        String conf = TypeConfigurationOperation.getTypeConfigurationFile(this.getFrameworkInstance(),
                this.getDataObjectType(), connectionTypeName);
        DataObjectOperation dataObjectOperation = new DataObjectOperation(conf);
        ObjectMapper objectMapper = new ObjectMapper();
        ConnectionType connectionType = objectMapper.convertValue(dataObjectOperation.getDataObject().getData(),
                ConnectionType.class);
        return connectionType;
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

	public FrameworkInstance getFrameworkInstance() {
		return frameworkInstance;
	}

	public void setFrameworkInstance(FrameworkInstance frameworkInstance) {
		this.frameworkInstance = frameworkInstance;
	}
}