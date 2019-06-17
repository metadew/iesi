package io.metadew.iesi.server.rest.resource.impersonation.dto;


import io.metadew.iesi.metadata.definition.ImpersonationParameter;
import org.springframework.hateoas.ResourceSupport;

public class ImpersonationParameterDto extends ResourceSupport {


    private String connection;
    private String impersonation;
    private String description;

    public ImpersonationParameterDto(){}

    public ImpersonationParameterDto(String connection, String impersonation, String description) {
        this.connection = connection;
        this.description = description;
        this.impersonation = impersonation;
    }
    public ImpersonationParameter convertToEntity() {
        return new ImpersonationParameter(connection, impersonation, description);
    }

    public static ImpersonationParameterDto convertToDto(ImpersonationParameter impersonationParameter) {
        return new ImpersonationParameterDto(impersonationParameter.getConnection(), impersonationParameter.getImpersonatedConnection(),
                impersonationParameter.getDescription());
    }

    public String getConnection() {
        return connection;
    }

    public void setConnection(String connection) {
        this.connection = connection;
    }

    public String getImpersonation() {
        return impersonation;
    }

    public void setImpersonation(String impersonation) {
        this.impersonation = impersonation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}


