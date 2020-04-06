package io.metadew.iesi.metadata.configuration.connection;

import io.metadew.iesi.metadata.definition.connection.ConnectionParameter;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionParameterKey;

public class ConnectionParameterBuilder {

    private final String parameterName;
    private final String environmentName;
    private String connectionName;
    private String value;

    public ConnectionParameterBuilder(String connectionName, String environmentName, String parameterName) {
        this.connectionName = connectionName;
        this.environmentName = environmentName;
        this.parameterName = parameterName;
    }

    public ConnectionParameterBuilder value(String value) {
        this.value = value;
        return this;
    }

    public ConnectionParameter build() {
        return new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey(connectionName, environmentName), parameterName), value == null ? "dummy" : value);
    }

}
