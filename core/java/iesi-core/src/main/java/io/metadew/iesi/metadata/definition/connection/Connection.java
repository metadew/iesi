package io.metadew.iesi.metadata.definition.connection;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;
import io.metadew.iesi.metadata.definition.environment.key.EnvironmentKey;
import lombok.EqualsAndHashCode;

import java.util.List;

@JsonDeserialize(using = ConnectionJsonComponent.Deserializer.class)
@JsonSerialize(using = ConnectionJsonComponent.Serializer.class)
@EqualsAndHashCode(callSuper = true)
public class Connection extends Metadata<ConnectionKey> {

    private String type;
    private String description;
    private List<ConnectionParameter> parameters;

    // Constructors

    public Connection(String name, String type, String description, String environment, List<ConnectionParameter> parameters) {
        super(new ConnectionKey(name, new EnvironmentKey(environment)));
        this.type = type;
        this.description = description;
        this.parameters = parameters;
    }
    public Connection(ConnectionKey connectionKey, String type, String description, List<ConnectionParameter> parameters) {
        super(connectionKey);
        this.type = type;
        this.description = description;
        this.parameters = parameters;
    }

    public List<ConnectionParameter> getParameters() {
        return parameters;
    }

    // Getters and Setters
    public String getName() {
        return getMetadataKey().getName();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public String getEnvironment() {
        return getMetadataKey().getEnvironmentKey().getName();
    }

}