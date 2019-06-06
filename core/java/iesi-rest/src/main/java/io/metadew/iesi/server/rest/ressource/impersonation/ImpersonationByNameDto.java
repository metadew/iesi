package io.metadew.iesi.server.rest.ressource.impersonation;

import io.metadew.iesi.metadata.definition.ImpersonationParameter;
import org.springframework.hateoas.ResourceSupport;

import java.util.List;

public class ImpersonationByNameDto extends ResourceSupport {

    private String name;
    private String description;
    private List<ImpersonationParameter> parameters;

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

    public List<ImpersonationParameter> getParameters() {
        return this.parameters;
    }

    public void setParameters(List<ImpersonationParameter> parameters) {
        this.parameters = parameters;
    }
}
