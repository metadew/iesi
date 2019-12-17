package io.metadew.iesi.metadata.definition.connection;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;

import java.util.List;

@JsonDeserialize(using = ConnectionJsonComponent.Deserializer.class)
@JsonSerialize(using = ConnectionJsonComponent.Serializer.class)
public class Connection extends Metadata<ConnectionKey> {

    private String type;
    private String description;
    private String environment;
    private List<ConnectionParameter> parameters;

    // Constructors

    public Connection(String name, String type, String description, String environment, List<ConnectionParameter> parameters) {
        super(new ConnectionKey(name));
        this.type = type;
        this.description = description;
        this.environment = environment;
        this.parameters = parameters;
    }
    public Connection(ConnectionKey connectionKey, String type, String description, String environment, List<ConnectionParameter> parameters) {
        super(connectionKey);
        this.type = type;
        this.description = description;
        this.environment = environment;
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
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }
}