package io.metadew.iesi.server.rest.resource.impersonation.dto;


import io.metadew.iesi.metadata.definition.impersonation.ImpersonationParameter;
import io.metadew.iesi.metadata.definition.impersonation.key.ImpersonationParameterKey;
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
    public ImpersonationParameter convertToEntity(String impersonationName) {
        return new ImpersonationParameter(new ImpersonationParameterKey(impersonationName, impersonation), connection, description);
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


