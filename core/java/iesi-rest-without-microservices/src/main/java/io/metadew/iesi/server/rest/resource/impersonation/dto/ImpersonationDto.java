package io.metadew.iesi.server.rest.resource.impersonation.dto;


import io.metadew.iesi.metadata.definition.impersonation.Impersonation;
import io.metadew.iesi.metadata.definition.impersonation.key.ImpersonationKey;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.ResourceSupport;

import java.util.List;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class ImpersonationDto extends ResourceSupport {
    private String name;
    private String description;
    private List<ImpersonationParameterDto> parameters;

    public Impersonation convertToEntity() {
        return new Impersonation(new ImpersonationKey(name), description,
                parameters.stream().map(parameter -> parameter.convertToEntity(name)).collect(Collectors.toList()));
    }

}


