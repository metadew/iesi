package io.metadew.iesi.server.rest.resource.connection.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.ResourceSupport;

import java.util.List;

public class ConnectionByNameDto extends ResourceSupport {

    @Setter private String name;
    @Getter @Setter private String type;
    @Getter @Setter private String description;

    private List<String> environments;

    public String getName() {
        return this.name;
    }

    public void setEnvironments(List<String> environments) {
        this.environments = environments;
    }

    public List<String> getEnvironments() {
        return this.environments;
    }
}
