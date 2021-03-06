package io.metadew.iesi.server.rest.environment.dto;

import io.metadew.iesi.metadata.definition.environment.Environment;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;
import java.util.stream.Collectors;


@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EnvironmentDto extends RepresentationModel<EnvironmentDto> {
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