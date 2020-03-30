package io.metadew.iesi.server.rest.resource.environment.dto;

import io.metadew.iesi.metadata.definition.environment.Environment;
import io.metadew.iesi.metadata.definition.environment.EnvironmentParameter;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;

public class EnvironmentDto extends RepresentationModel<EnvironmentDto> {
    @Getter @Setter private String name;
    @Getter @Setter private String description;
    @Getter @Setter private List<EnvironmentParameter> parameters;

    public EnvironmentDto(Environment environment){}

    public EnvironmentDto(String name, String description, List<EnvironmentParameter> parameters) {
        this.name = name;
        this.description = description;
        this.parameters = parameters;
    }
    public Environment convertToEntity() {
        return new Environment(name, description, parameters);
    }

    public String getName() {
        return name;
    }


    public String getDescription() {
        return description;
    }


    public List<EnvironmentParameter> getParameters() {
        return parameters;
    }

}