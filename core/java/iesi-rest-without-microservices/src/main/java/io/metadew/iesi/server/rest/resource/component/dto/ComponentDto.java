package io.metadew.iesi.server.rest.resource.component.dto;

import io.metadew.iesi.metadata.definition.component.Component;
import io.metadew.iesi.metadata.definition.component.ComponentAttribute;
import io.metadew.iesi.metadata.definition.component.ComponentParameter;
import org.springframework.hateoas.ResourceSupport;

import java.util.List;
import java.util.Objects;


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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ComponentDto)) return false;
        if (!super.equals(o)) return false;
        ComponentDto that = (ComponentDto) o;
        return getType().equals(that.getType()) &&
                getName().equals(that.getName()) &&
                getDescription().equals(that.getDescription()) &&
                getVersion().equals(that.getVersion()) &&
                getParameters().equals(that.getParameters()) &&
                getAttributes().equals(that.getAttributes());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getType(), getName(), getDescription(), getVersion(), getParameters(), getAttributes());
    }

    public Component convertToEntity() {
        return new Component(
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ComponentVersionDto getVersion() {
        return version;
    }

    public void setVersion(ComponentVersionDto version) {
        this.version = version;
    }

    public List<ComponentParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<ComponentParameter> parameters) {
        this.parameters = parameters;
    }

    public List<ComponentAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<ComponentAttribute> attributes) {
        this.attributes = attributes;
    }

}