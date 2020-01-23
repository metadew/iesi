package io.metadew.iesi.metadata.definition.connection.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
public class ConnectionParameterKey extends MetadataKey {


    private ConnectionKey connectionKey;
    private String parameterName;

    public ConnectionParameterKey(String connectionName, String environment, String parameterName){
        this.connectionKey = new ConnectionKey(connectionName, environment);
        this.parameterName = parameterName;
    }

    public ConnectionParameterKey(ConnectionKey connectionKey, String parameterName){
        this.connectionKey = connectionKey;
        this.parameterName = parameterName;
    }

    public String getParameterName() {
        return parameterName;
    }

    public ConnectionKey getConnectionKey() {
        return connectionKey;
    }
}
