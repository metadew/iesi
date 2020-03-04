package io.metadew.iesi.metadata.definition.connection;


import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionParameterKey;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class ConnectionParameter extends Metadata<ConnectionParameterKey> {

    private String value;

    public ConnectionParameter(ConnectionParameterKey connectionParameterKey, String value) {
        super(connectionParameterKey);
        this.value = value;
    }

    public ConnectionParameter(String connectionName, String environmentName, String name, String value) {
        super(new ConnectionParameterKey(connectionName, environmentName, name));
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName(){
        return getMetadataKey().getParameterName();
    }

}