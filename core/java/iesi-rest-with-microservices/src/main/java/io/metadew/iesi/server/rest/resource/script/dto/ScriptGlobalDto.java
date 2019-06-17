package io.metadew.iesi.server.rest.resource.script.dto;

import org.springframework.hateoas.ResourceSupport;
import lombok.Getter;
import lombok.Setter;

public class ScriptGlobalDto extends ResourceSupport {

    @Setter private String name;
    @Getter @Setter private String description;

    public String getName() {
        return this.name;
    }
}

