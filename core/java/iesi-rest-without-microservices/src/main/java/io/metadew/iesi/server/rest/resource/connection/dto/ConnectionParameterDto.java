package io.metadew.iesi.server.rest.resource.connection.dto;

import io.metadew.iesi.metadata.definition.component.ComponentParameter;
import io.metadew.iesi.metadata.definition.component.key.ComponentKey;
import io.metadew.iesi.metadata.definition.component.key.ComponentParameterKey;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.ConnectionParameter;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionParameterKey;
import io.metadew.iesi.server.rest.resource.Dto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ConnectionParameterDto extends Dto {

    private final String name;
    private final String value;

    public ConnectionParameter convertToEntity(String name, String environment) {
        return new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey(name, environment), this.name), value);
    }

}