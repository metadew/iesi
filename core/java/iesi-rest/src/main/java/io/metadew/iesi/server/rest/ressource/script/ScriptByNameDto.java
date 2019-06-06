package io.metadew.iesi.server.rest.ressource.script;

import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.ResourceSupport;

import java.util.List;

public class ScriptByNameDto extends ResourceSupport {

    @Getter @Setter private String name;
    @Getter @Setter private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}


