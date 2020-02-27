package io.metadew.iesi.server.rest.resource.environment.dto;

import io.metadew.iesi.metadata.definition.environment.EnvironmentParameter;
import io.metadew.iesi.metadata.definition.environment.key.EnvironmentParameterKey;
import io.metadew.iesi.server.rest.resource.Dto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class EnvironmentParameterDto extends Dto {

    private final String name;
    private final String value;

    public EnvironmentParameter convertToEntity(String environment) {
        return new EnvironmentParameter(new EnvironmentParameterKey(environment, name), value);
    }

}