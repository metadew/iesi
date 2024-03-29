package io.metadew.iesi.metadata.definition.connection;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.metadew.iesi.metadata.definition.SecuredObject;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;
import io.metadew.iesi.metadata.definition.environment.key.EnvironmentKey;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@JsonDeserialize(using = ConnectionJsonComponent.Deserializer.class)
@JsonSerialize(using = ConnectionJsonComponent.Serializer.class)
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Connection extends SecuredObject<ConnectionKey> {

    private String type;
    private String description;
    private List<ConnectionParameter> parameters;

    // Constructors

    public Connection(String name, SecurityGroupKey securityGroupKey, String securityGroupName, String type, String description, String environment,
                      List<ConnectionParameter> parameters) {
        super(new ConnectionKey(name, new EnvironmentKey(environment)), securityGroupKey, securityGroupName);
        this.type = type;
        this.description = description;
        this.parameters = parameters;
    }

    @Builder
    public Connection(ConnectionKey connectionKey, SecurityGroupKey securityGroupKey, String securityGroupName, String type, String description,
                      List<ConnectionParameter> parameters) {
        super(connectionKey, securityGroupKey, securityGroupName);
        this.type = type;
        this.description = description;
        this.parameters = parameters;
    }

}