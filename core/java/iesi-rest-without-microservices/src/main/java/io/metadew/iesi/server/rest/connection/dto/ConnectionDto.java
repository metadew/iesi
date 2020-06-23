package io.metadew.iesi.server.rest.connection.dto;


import io.metadew.iesi.metadata.definition.connection.Connection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class ConnectionDto extends RepresentationModel<ConnectionDto> {

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
