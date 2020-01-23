package io.metadew.iesi.metadata.configuration.component;

import io.metadew.iesi.metadata.configuration.connection.ConnectionParameterBuilder;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.ConnectionParameter;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ComponentBuilder {

    private int numberOfParameters = 0;
    private List<ConnectionParameter> connectionParameters = new ArrayList<>();
    private String connectionName;
    private String environmentName;
    private String description;
    private String type;

    public ComponentBuilder(String connectionName, String environmentName) {
        this.connectionName = connectionName;
        this.environmentName = environmentName;
    }

    public ComponentBuilder description(String description) {
        this.description = description;
        return this;
    }


    public ComponentBuilder type(String type) {
        this.type = type;
        return this;
    }

    public ComponentBuilder addEnvironmentParameter(ConnectionParameter environmentParameter) {
        this.connectionParameters.add(environmentParameter);
        return this;
    }

    public ComponentBuilder numberOfParameters(int numberOfParameters) {
        this.numberOfParameters = numberOfParameters;
        return this;
    }

    public Connection build() {
        connectionParameters.addAll(IntStream.range(0, numberOfParameters)
                .boxed()
                .map(i -> new ConnectionParameterBuilder(connectionName, environmentName, "parameter" + i).build())
                .collect(Collectors.toList()));
        return new Connection(new ConnectionKey(connectionName, environmentName), type == null ? "dummy" : type, description == null ? "dummy" : description, connectionParameters);
    }

}
