package io.metadew.iesi.server.rest.resource.script.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

public class ScriptGlobalDto extends RepresentationModel<ScriptGlobalDto> {

    @Setter private String name;
    @Getter @Setter private String description;

    public String getName() {
        return this.name;
    }
}

