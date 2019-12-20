package io.metadew.iesi.metadata.definition.component;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.component.key.ComponentKey;

import java.util.ArrayList;
import java.util.List;

@JsonDeserialize(using = ComponentJsonComponent.Deserializer.class)
@JsonSerialize(using = ComponentJsonComponent.Serializer.class)
public class Component extends Metadata<ComponentKey> {

    private String type;
    private String name;
    private String description;
    private ComponentVersion version;
    private List<ComponentParameter> parameters = new ArrayList<>();
    private List<ComponentAttribute> attributes = new ArrayList<>();

    public Component(ComponentKey componentKey, String type, String name, String description, ComponentVersion version,
                     List<ComponentParameter> parameters, List<ComponentAttribute> attributes) {
        super(componentKey);
        this.type = type;
        this.name = name;
        this.description = description;
        this.version = version;
        this.parameters = parameters;
        this.attributes = attributes;
    }

    public Component(String id, String type, String name, String description, ComponentVersion version,
                     List<ComponentParameter> parameters, List<ComponentAttribute> attributes) {
        super(new ComponentKey(id, version.getNumber()));
        this.type = type;
        this.name = name;
        this.description = description;
        this.version = version;
        this.parameters = parameters;
        this.attributes = attributes;
    }

    //Getters and Setters
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return getMetadataKey().getId();
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

    public ComponentVersion getVersion() {
        return version;
    }

    public void setVersion(ComponentVersion version) {
        this.version = version;
    }

	public boolean isEmpty() {
		return (getName() == null || getName().isEmpty()) ;
	}

}