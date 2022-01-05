package io.metadew.iesi.metadata.definition.component;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.metadew.iesi.metadata.definition.SecuredObject;
import io.metadew.iesi.metadata.definition.component.key.ComponentKey;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@JsonDeserialize(using = ComponentJsonComponent.Deserializer.class)
@JsonSerialize(using = ComponentJsonComponent.Serializer.class)
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Component extends SecuredObject<ComponentKey> {

    private String type;
    private String name;
    private String description;
    private ComponentVersion version;
    private List<ComponentParameter> parameters;
    private List<ComponentAttribute> attributes;

    @Builder
    public Component(ComponentKey componentKey, SecurityGroupKey securityGroupKey, String securityGroupName,
                     String type, String name, String description, ComponentVersion version,
                     List<ComponentParameter> parameters, List<ComponentAttribute> attributes) {
        super(componentKey, securityGroupKey, securityGroupName);
        this.type = type;
        this.name = name;
        this.description = description;
        this.version = version;
        this.parameters = parameters;
        this.attributes = attributes;
    }
}