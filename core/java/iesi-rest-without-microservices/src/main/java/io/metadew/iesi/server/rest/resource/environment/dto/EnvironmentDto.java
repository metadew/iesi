package io.metadew.iesi.server.rest.resource.environment.dto;

import io.metadew.iesi.metadata.definition.environment.Environment;
import io.metadew.iesi.metadata.definition.environment.EnvironmentParameter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.ResourceSupport;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class EnvironmentDto extends ResourceSupport {
    private String name;
    private String description;
    private List<EnvironmentParameter> parameters;

    public Environment convertToEntity() {
        return new Environment(name, description, parameters);
    }

}