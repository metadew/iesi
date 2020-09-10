package io.metadew.iesi.metadata.definition.component;


import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.component.key.ComponentParameterKey;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ComponentParameter extends Metadata<ComponentParameterKey> {

    private String value;
    @Builder
    public ComponentParameter(ComponentParameterKey componentParameterKey, String value) {
        super(componentParameterKey);
        this.value = value;
    }

}