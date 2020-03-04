package io.metadew.iesi.metadata.definition.connection.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;
import lombok.Data;
import lombok.EqualsAndHashCode;
@Data
@EqualsAndHashCode(callSuper = true)
public class ConnectionParameterKey extends MetadataKey {

    private final ConnectionKey connectionKey;
    private final String parameterName;

    public ConnectionParameterKey(String connectionName, String environment, String parameterName){
        this.connectionKey = new ConnectionKey(connectionName, environment);
        this.parameterName = parameterName;
    }

}
