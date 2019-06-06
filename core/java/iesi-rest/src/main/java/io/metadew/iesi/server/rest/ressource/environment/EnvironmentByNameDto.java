package io.metadew.iesi.server.rest.ressource.environment;

import io.metadew.iesi.metadata.definition.EnvironmentParameter;
import org.springframework.hateoas.ResourceSupport;

import java.util.List;

public class EnvironmentByNameDto extends ResourceSupport {

     private String name;
     private String description;
     private List<EnvironmentParameter> parameters;

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return description;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<EnvironmentParameter> getParameters() {
        return this.parameters;
    }

    public void setParameters(List<EnvironmentParameter> parameters) {
        this.parameters = parameters;
    }
}



