package io.metadew.iesi.metadata.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.ConnectionType;
import io.metadew.iesi.metadata.operation.DataObjectOperation;
import io.metadew.iesi.metadata.operation.TypeConfigurationOperation;

public class ConnectionTypeConfiguration {

    private ConnectionType connectionType;
    private FrameworkExecution frameworkExecution;
    private String dataObjectType = "ConnectionType";

    // Constructors
    public ConnectionTypeConfiguration(ConnectionType connectionType, FrameworkExecution frameworkExecution) {
        this.setConnectionType(connectionType);
        this.setFrameworkExecution(frameworkExecution);
    }

    public ConnectionTypeConfiguration(FrameworkExecution frameworkExecution) {
        this.setFrameworkExecution(frameworkExecution);
    }

    public ConnectionType getConnectionType(String connectionTypeName) {
        String conf = TypeConfigurationOperation.getTypeConfigurationFile(this.getFrameworkExecution(),
                this.getDataObjectType(), connectionTypeName);
        DataObjectOperation dataObjectOperation = new DataObjectOperation(this.getFrameworkExecution(), conf);
        ObjectMapper objectMapper = new ObjectMapper();
        ConnectionType connectionType = objectMapper.convertValue(dataObjectOperation.getDataObject().getData(),
                ConnectionType.class);
        return connectionType;
    }

    // Getters and Setters
    public FrameworkExecution getFrameworkExecution() {
        return frameworkExecution;
    }

    public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
        this.frameworkExecution = frameworkExecution;
    }

    public ConnectionType getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(ConnectionType connectionType) {
        this.connectionType = connectionType;
    }

    public void setConnectionType(FrameworkExecution frameworkExecution) {
        this.setFrameworkExecution(frameworkExecution);
    }

    public String getDataObjectType() {
        return dataObjectType;
    }

    public void setDataObjectType(String dataObjectType) {
        this.dataObjectType = dataObjectType;
    }
}