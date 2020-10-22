package io.metadew.iesi.metadata.definition.component;


import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.component.key.ComponentBuildKey;
import lombok.Builder;

public class ComponentBuild extends Metadata<ComponentBuildKey> {

    private String description;

    @Builder
    public ComponentBuild(ComponentBuildKey componentBuildKey, String description) {
        super(componentBuildKey);
        this.description = description;
    }

    //Getters and Setters
    public String getName() {
        return getMetadataKey().getComponentBuildName();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}