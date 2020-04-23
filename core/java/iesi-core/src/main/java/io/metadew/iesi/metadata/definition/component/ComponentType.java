package io.metadew.iesi.metadata.definition.component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComponentType {

    private String name;
    private String description;
    private Map<String, ComponentTypeParameter> parameters;


}