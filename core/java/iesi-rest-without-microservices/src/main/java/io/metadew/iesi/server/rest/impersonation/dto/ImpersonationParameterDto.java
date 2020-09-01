package io.metadew.iesi.server.rest.impersonation.dto;


import io.metadew.iesi.metadata.definition.impersonation.ImpersonationParameter;
import io.metadew.iesi.metadata.definition.impersonation.key.ImpersonationKey;
import io.metadew.iesi.metadata.definition.impersonation.key.ImpersonationParameterKey;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class ImpersonationParameterDto extends RepresentationModel<ImpersonationParameterDto> {

    private String connection;
    private String impersonation;
    private String description;

    public ImpersonationParameter convertToEntity(String impersonationName) {
        return new ImpersonationParameter(new ImpersonationParameterKey(new ImpersonationKey(impersonationName), connection), impersonation, description);
    }

}


