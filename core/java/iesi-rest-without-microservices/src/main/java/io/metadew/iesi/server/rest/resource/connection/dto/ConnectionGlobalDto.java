package io.metadew.iesi.server.rest.resource.connection.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

public class ConnectionGlobalDto extends RepresentationModel<ConnectionGlobalDto> {

    @Setter private String name;
    @Getter @Setter private String type;
    @Getter @Setter private String description;

    public String getName() {
        return this.name;
    }
}
