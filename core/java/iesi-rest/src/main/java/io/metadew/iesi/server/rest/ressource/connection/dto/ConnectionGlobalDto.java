package io.metadew.iesi.server.rest.ressource.connection.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.ResourceSupport;

public class ConnectionGlobalDto extends ResourceSupport {

    @Setter private String name;
    @Getter @Setter private String type;
    @Getter @Setter private String description;

    public String getName() {
        return this.name;
    }
}
