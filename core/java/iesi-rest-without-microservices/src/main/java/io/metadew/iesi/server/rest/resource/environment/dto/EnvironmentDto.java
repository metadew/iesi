package io.metadew.iesi.server.rest.resource.environment.dto;

import io.metadew.iesi.metadata.definition.environment.Environment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.ResourceSupport;

import java.util.List;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class EnvironmentDto extends ResourceSupport {
    private String name;
    private String description;
    private List<EnvironmentParameterDto> parameters;

    public Environment convertToEntity() {
        return new Environment(name, description,
                parameters.stream()
                        .map(parameter -> parameter.convertToEntity(name))
                        .collect(Collectors.toList()));
    }

}