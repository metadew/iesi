package io.metadew.iesi.server.rest.ressource.environment;

import io.metadew.iesi.metadata.definition.EnvironmentParameter;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.ResourceSupport;

import java.util.List;

public class EnvironmentGlobalDto extends ResourceSupport {

    @Setter
    private String name;
    @Getter @Setter private String description;
    @Getter @Setter private List<EnvironmentParameter> parameters;

    public String getName() {
        return this.name;
    }
}
