package io.metadew.iesi.server.rest.ressource.environment;

import io.metadew.iesi.metadata.definition.Environment;
import io.metadew.iesi.metadata.definition.EnvironmentParameter;
import org.springframework.hateoas.ResourceSupport;

import java.util.List;

public class EnvironmentDto extends ResourceSupport {
    private String name;
    private String description;
    private List<EnvironmentParameter> parameters;

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

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<EnvironmentParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<EnvironmentParameter> parameters) {
        this.parameters = parameters;
    }
}
