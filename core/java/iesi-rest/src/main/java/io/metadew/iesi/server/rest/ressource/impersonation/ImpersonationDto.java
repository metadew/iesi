package io.metadew.iesi.server.rest.ressource.impersonation;


import io.metadew.iesi.metadata.definition.Impersonation;
import io.metadew.iesi.metadata.definition.ImpersonationParameter;
import org.springframework.hateoas.ResourceSupport;

import java.util.List;

public class ImpersonationDto extends ResourceSupport {
    private String name;
    private String description;
    private List<ImpersonationParameter> parameters;

    public ImpersonationDto(){}

    public ImpersonationDto(String name, String description, List<ImpersonationParameter> parameters) {
        this.name = name;
        this.description = description;
        this.parameters = parameters;
    }
    public Impersonation convertToEntity() {
        return new Impersonation(name, description, parameters);
    }

    public static ImpersonationDto convertToDto(Impersonation impersonation) {
        return new ImpersonationDto(impersonation.getName(), impersonation.getDescription(), impersonation.getParameters());
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

    public List<ImpersonationParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<ImpersonationParameter> parameters) {
        this.parameters = parameters;
    }
}


