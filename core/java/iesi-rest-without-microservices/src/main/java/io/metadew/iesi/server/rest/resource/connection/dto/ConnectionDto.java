package io.metadew.iesi.server.rest.resource.connection.dto;


import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.ConnectionParameter;
import io.metadew.iesi.server.rest.resource.Dto;
import lombok.*;
import org.springframework.hateoas.ResourceSupport;

import java.util.List;
import java.util.stream.Collectors;


@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class ConnectionDto extends Dto {

    private String name;
    private String type;
    private String description;
    private String environment;
    private List<ConnectionParameterDto> parameters;

    public Connection convertToEntity() {
        return new Connection(name, type, description, environment,
                parameters.stream().map(parameter -> parameter.convertToEntity(name, environment)).collect(Collectors.toList()));
    }

}
