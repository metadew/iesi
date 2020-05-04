package io.metadew.iesi.server.rest.resource.environment.dto;

import io.metadew.iesi.metadata.definition.environment.EnvironmentParameter;
import io.metadew.iesi.metadata.definition.environment.key.EnvironmentKey;
import io.metadew.iesi.metadata.definition.environment.key.EnvironmentParameterKey;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

@Data
@EqualsAndHashCode(callSuper = false)
public class EnvironmentParameterDto extends RepresentationModel<EnvironmentParameterDto> {

    private final String name;
    private final String value;

    public EnvironmentParameter convertToEntity(String environment) {
        return new EnvironmentParameter(new EnvironmentParameterKey(new EnvironmentKey(environment), name), value);
    }

}