package io.metadew.iesi.server.rest.ressource.component;

import java.util.HashMap;
import java.util.List;

import io.metadew.iesi.metadata.definition.Component;
import io.metadew.iesi.metadata.definition.ComponentAttribute;
import io.metadew.iesi.metadata.definition.ComponentParameter;
import io.metadew.iesi.metadata.definition.ComponentVersion;

import org.springframework.hateoas.ResourceSupport;

public class ComponentPostByNameDto extends ResourceSupport {

    private String type;
    private String name;
    private String description;
    private HashMap<String, Object> versions;
    private ComponentVersion version;
    private List<ComponentParameter> parameters;
    private List<ComponentAttribute> attributes;

    public ComponentPostByNameDto (List<Component> component) {
        super();
        this.type = component.get(0).getType();
        this.name = component.get(0).getName();
        this.description = component.get(0).getDescription();
        this.versions = hashversions(component);
        this.parameters = component.get(0).getParameters();
        this.attributes = component.get(0).getAttributes();
    }

    public ComponentPostByNameDto(String type, String name, String description, ComponentVersion version,
                                  List<ComponentParameter> parameters, List<ComponentAttribute> attributes) {
        super();
        this.type = type;
        this.name = name;
        this.description = description;
        this.version = version;
        this.parameters = parameters;
        this.attributes = attributes;
    }

    private HashMap<String, Object> hashversions(List<Component> component) {
        HashMap<String, Object> newVersions = new HashMap<String, Object>();
        for (Component comp : component) {
            Long number = comp.getVersion().getNumber();
            String description = comp.getVersion().getDescription();
            newVersions.put("number", number);
            newVersions.put("description", description);
        }
        return newVersions;
    }
    public Component convertToEntity() { return new Component(
            type, name, description, version, parameters, attributes);
    }

    public static ComponentPostByNameDto convertToDto(List<Component> component) {
        return new  ComponentPostByNameDto(component);
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

    public HashMap<String, Object> getVersions() {
        return versions;
    }

    public void setVersions(HashMap<String, Object> versions) {
        this.versions = versions;
    }

    public List<ComponentParameter> getParameters() {
        return parameters;
    }

    public List<ComponentAttribute> getAttributes() {
        return attributes;
    }

    /**
     * @return the version
     */
    public ComponentVersion getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(ComponentVersion version) {
        this.version = version;
    }

    /**
     * @param parameters the parameters to set
     */
    public void setParameters(List<ComponentParameter> parameters) {
        this.parameters = parameters;
    }

    /**
     * @param attributes the attributes to set
     */
    public void setAttributes(List<ComponentAttribute> attributes) {
        this.attributes = attributes;
    }

}