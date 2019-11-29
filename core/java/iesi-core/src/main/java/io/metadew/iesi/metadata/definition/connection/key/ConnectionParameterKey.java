package io.metadew.iesi.metadata.definition.connection.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;

public class ConnectionParameterKey extends MetadataKey {

    private String connectionName;
    private String environment;
    private String parameterName;

    public ConnectionParameterKey(String connectionName, String environment, String parameterName){
        this.connectionName = connectionName;
        this.environment = environment;
        this.parameterName = parameterName;
    }

    public String getConnectionName() {
        return connectionName;
    }

    public String getEnvironment() {
        return environment;
    }

    public String getParameterName() {
        return parameterName;
    }
}
