package io.metadew.iesi.metadata.definition.component;


import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.component.key.ComponentAttributeKey;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ComponentAttribute extends Metadata<ComponentAttributeKey> {

    private String value;

    public ComponentAttribute(ComponentAttributeKey componentAttributeKey, String value) {
        super(componentAttributeKey);
        this.value = value;
    }

}