package io.metadew.iesi.metadata.configuration.connection;

import io.metadew.iesi.metadata.configuration.script.ScriptBuilder;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.ConnectionParameter;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;
import io.metadew.iesi.metadata.definition.security.SecurityGroup;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import io.metadew.iesi.metadata.service.security.SecurityGroupService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ConnectionBuilder {

    private int numberOfParameters = 0;
    private List<ConnectionParameter> connectionParameters = new ArrayList<>();
    private String connectionName;
    private SecurityGroupKey securityGroupKey;
    private String securityGroupName;
    private String environmentName;
    private String description;
    private String type;

    public ConnectionBuilder(String connectionName, String environmentName) {
        this.connectionName = connectionName;
        this.environmentName = environmentName;
    }

    public ConnectionBuilder securityGroupName(String securityGroupName) {
        this.securityGroupName = securityGroupName;
        return this;
    }


    public ConnectionBuilder securityGroupKey(SecurityGroupKey securityGroupKey) {
        this.securityGroupKey = securityGroupKey;
        return this;
    }

    public ConnectionBuilder description(String description) {
        this.description = description;
        return this;
    }


    public ConnectionBuilder type(String type) {
        this.type = type;
        return this;
    }

    public ConnectionBuilder addEnvironmentParameter(ConnectionParameter environmentParameter) {
        this.connectionParameters.add(environmentParameter);
        return this;
    }

    public ConnectionBuilder numberOfParameters(int numberOfParameters) {
        this.numberOfParameters = numberOfParameters;
        return this;
    }

    public Connection build() {
        if (securityGroupName == null) {
            securityGroupName = "DEFAULT";
        }

        if (securityGroupKey == null) {
            securityGroupKey = SecurityGroupService.getInstance().get(securityGroupName)
                    .map(SecurityGroup::getMetadataKey)
                    .orElseThrow(() -> new RuntimeException("Could not find Security Group with name" + securityGroupName));
        }

        connectionParameters.addAll(IntStream.range(0, numberOfParameters)
                .boxed()
                .map(i -> new ConnectionParameterBuilder(connectionName, environmentName, "parameter" + i).build())
                .collect(Collectors.toList()));
        return new Connection(
                new ConnectionKey(connectionName, environmentName),
                securityGroupKey,
                securityGroupName,
                type == null ? "dummy" : type,
                description == null ? "dummy" : description,
                connectionParameters);
    }

}
