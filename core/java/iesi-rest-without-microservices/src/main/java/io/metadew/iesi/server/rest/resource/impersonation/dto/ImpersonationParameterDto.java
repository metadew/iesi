package io.metadew.iesi.server.rest.resource.impersonation.dto;


import io.metadew.iesi.metadata.definition.impersonation.ImpersonationParameter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.ResourceSupport;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class ImpersonationParameterDto extends ResourceSupport {

    private String connection;
    private String impersonation;
    private String description;

    public ImpersonationParameter convertToEntity(String impersonationName) {
        return new ImpersonationParameter(impersonationName, connection, impersonation, description);
    }

}


