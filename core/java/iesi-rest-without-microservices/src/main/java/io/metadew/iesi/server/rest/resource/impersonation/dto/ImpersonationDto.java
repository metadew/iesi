package io.metadew.iesi.server.rest.resource.impersonation.dto;


import io.metadew.iesi.metadata.definition.impersonation.Impersonation;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;
import java.util.stream.Collectors;

public class ImpersonationDto extends RepresentationModel<ImpersonationDto> {
    private String name;
    private String description;
    private List<ImpersonationParameterDto> parameters;

    public ImpersonationDto(){}

    public ImpersonationDto(String name, String description, List<ImpersonationParameterDto> parameters) {
        this.name = name;
        this.description = description;
        this.parameters = parameters;
    }
    public Impersonation convertToEntity() {
        return new Impersonation(name, description, parameters.stream().map(parameter -> parameter.convertToEntity(name)).collect(Collectors.toList()));
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

    public List<ImpersonationParameterDto> getParameters() {
        return parameters;
    }

    public void setParameters(List<ImpersonationParameterDto> parameters) {
        this.parameters = parameters;
    }
}


