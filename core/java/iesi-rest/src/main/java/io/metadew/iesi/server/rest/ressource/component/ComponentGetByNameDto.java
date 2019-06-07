package io.metadew.iesi.server.rest.ressource.component;

import io.metadew.iesi.metadata.definition.Component;
import org.springframework.hateoas.ResourceSupport;

import java.util.List;
import java.util.stream.Collectors;

public class ComponentGetByNameDto extends ResourceSupport {

    private String type;
    private String name;
    private String description;
    private List<String> versions;

    public ComponentGetByNameDto(List<Component> component) {
        super();
        this.type = component.get(0).getType();
        this.name = component.get(0).getName();
        this.description = component.get(0).getDescription();
        this.versions = component.stream().map(components -> components.getVersion().getDescription())
                .collect(Collectors.toList());

    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getVersions() {
        return versions;
    }

}
