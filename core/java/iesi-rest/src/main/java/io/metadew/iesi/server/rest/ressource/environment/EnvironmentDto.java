package io.metadew.iesi.server.rest.ressource.environment;

import io.metadew.iesi.metadata.definition.Environment;
import io.metadew.iesi.metadata.definition.EnvironmentParameter;
import lombok.Getter;
import lombok.Setter;

import org.springframework.hateoas.ResourceSupport;

import java.util.List;

public class EnvironmentDto extends ResourceSupport {
	 @Getter @Setter private String name;
	 @Getter @Setter private String description;
	 @Getter @Setter private List<EnvironmentParameter> parameters;

    public EnvironmentDto(){}

    public EnvironmentDto(String name, String description, List<EnvironmentParameter> parameters) {
        this.name = name;
        this.description = description;
        this.parameters = parameters;
    }
    public Environment convertToEntity() {
        return new Environment(name, description, parameters);
    }

    public static EnvironmentDto convertToDto(Environment environment) {
        return new EnvironmentDto(environment.getName(), environment.getDescription(), environment.getParameters());
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
