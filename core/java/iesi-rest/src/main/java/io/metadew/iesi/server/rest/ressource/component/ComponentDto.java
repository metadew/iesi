package io.metadew.iesi.server.rest.ressource.component;

import io.metadew.iesi.metadata.definition.Component;
import io.metadew.iesi.metadata.definition.ComponentAttribute;
import io.metadew.iesi.metadata.definition.ComponentParameter;
import io.metadew.iesi.metadata.definition.ComponentVersion;
import org.springframework.hateoas.ResourceSupport;

import java.util.HashMap;
import java.util.List;

public class ComponentDto extends ResourceSupport {

    private String type;
    private String name;
    private String description;
    private ComponentVersionDto version;
    private List<ComponentParameter> parameters;
    private List<ComponentAttribute> attributes;

    public ComponentDto() {}


    public ComponentDto(String type, String name, String description, ComponentVersionDto version,
                        List<ComponentParameter> parameters, List<ComponentAttribute> attributes) {
        super();
        this.type = type;
        this.name = name;
        this.description = description;
        this.version = version;
        this.parameters = parameters;
        this.attributes = attributes;
    }

    public Component convertToEntity() { return new Component(
            type, name, description, version.convertToEntity(), parameters, attributes);
    }

    public static ComponentDto convertToDto(Component component) {
        return new ComponentDto(component.getType(), component.getName(), component.getDescription(),
                ComponentVersionDto.convertToDto(component.getVersion()), component.getParameters(), component.getAttributes());
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}