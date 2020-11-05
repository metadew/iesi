package io.metadew.iesi.metadata.definition.environment;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.component.ComponentParameter;
import io.metadew.iesi.metadata.definition.environment.key.EnvironmentKey;
import lombok.Builder;
import lombok.EqualsAndHashCode;

import java.util.List;

@JsonDeserialize(using = EnvironmentJsonComponent.Deserializer.class)
@JsonSerialize(using = EnvironmentJsonComponent.Serializer.class)
@EqualsAndHashCode(callSuper = true)
public class Environment extends Metadata<EnvironmentKey> {

    private String description;
    private List<EnvironmentParameter> parameters;

    public Environment(String name, String description, List<EnvironmentParameter> parameters) {
        super(new EnvironmentKey(name));
        this.description = description;
        this.parameters = parameters;
    }

    @Builder
    public Environment(EnvironmentKey environmentKey, String description, List<EnvironmentParameter> parameters) {
        super(environmentKey);
        this.description = description;
        this.parameters = parameters;
    }

    public Environment(EnvironmentKey environmentKey) {
        super(environmentKey);
    }

    //Getters and Setters
    public String getName() {
        return getMetadataKey().getName();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<EnvironmentParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<EnvironmentParameter> parameters) {
        this.parameters = parameters;
    }

    public boolean isEmpty() {
        return (getName() == null || getName().isEmpty());
    }

    public void addParameters(EnvironmentParameter parameters) {
        this.parameters.add(parameters);
    }
}