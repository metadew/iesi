package io.metadew.iesi.metadata.configuration.component;

import io.metadew.iesi.metadata.definition.component.Component;
import io.metadew.iesi.metadata.definition.component.ComponentAttribute;
import io.metadew.iesi.metadata.definition.component.ComponentParameter;
import io.metadew.iesi.metadata.definition.component.ComponentVersion;
import io.metadew.iesi.metadata.definition.component.key.ComponentKey;
import io.metadew.iesi.metadata.definition.component.key.ComponentVersionKey;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ComponentBuilder {

    private int numberOfParameters = 0;
    private List<ComponentParameter> componentParameters = new ArrayList<>();
    private List<ComponentAttribute> componentAttributes = new ArrayList<>();
    private String componentID;
    private long version;
    private String description;
    private String type;
    private int numberOfAttributes = 0;
    private String name;

    public ComponentBuilder(String componentID, long version) {
        this.componentID = componentID;
        this.version = version;
    }

    public ComponentBuilder description(String description) {
        this.description = description;
        return this;
    }


    public ComponentBuilder type(String type) {
        this.type = type;
        return this;
    }

    public ComponentBuilder name(String name) {
        this.name = name;
        return this;
    }

    public ComponentBuilder addComponentParameter(ComponentParameter componentParameter) {
        this.componentParameters.add(componentParameter);
        return this;
    }

    public ComponentBuilder addComponentAttribute(ComponentAttribute componentAttribute) {
        this.componentAttributes.add(componentAttribute);
        return this;
    }

    public ComponentBuilder numberOfParameters(int numberOfParameters) {
        this.numberOfParameters = numberOfParameters;
        return this;
    }

    public ComponentBuilder numberOfAttributes(int numberOfAttributes) {
        this.numberOfAttributes = numberOfAttributes;
        return this;
    }

    public Component build() {
        componentParameters.addAll(IntStream.range(0, numberOfParameters)
                .boxed()
                .map(i -> new ComponentParameterBuilder(componentID, version, "parameter" + i).build())
                .collect(Collectors.toList()));
        componentAttributes.addAll(IntStream.range(0, numberOfAttributes)
                .boxed()
                .map(i -> new ComponentAttributeBuilder(componentID, version, "env", "parameter" + i).build())
                .collect(Collectors.toList()));
        return new Component(new ComponentKey(componentID, version),
                name == null ? "name" : name,
                type == null ? "dummy" : type,
                description == null ? "dummy" : description,
                new ComponentVersion(new ComponentVersionKey(componentID, version), "description"),
                componentParameters,
                componentAttributes);
    }

}
