package io.metadew.iesi.server.rest.ressource.impersonation;

import io.metadew.iesi.metadata.definition.ImpersonationParameter;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.ResourceSupport;

import java.util.List;

public class ImpersonationGlobalDto extends ResourceSupport {

    @Setter
    private String name;
    @Getter @Setter private String description;
    @Getter @Setter private List<ImpersonationParameter> parameters;

    public String getName() {
        return this.name;
    }
}