package io.metadew.iesi.server.rest.ressource.script;

import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.ResourceSupport;

public class ScriptByNameDto extends ResourceSupport {

    @Setter private String name;
    @Getter @Setter private String description;

    public String getName() {
        return this.name;
    }

}