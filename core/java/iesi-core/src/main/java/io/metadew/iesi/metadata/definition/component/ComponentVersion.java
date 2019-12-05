package io.metadew.iesi.metadata.definition.component;

import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.component.key.ComponentVersionKey;

import java.util.ArrayList;
import java.util.List;

public class ComponentVersion extends Metadata<ComponentVersionKey> {

    private String description;
    private List<ComponentBuild> builds;

    public ComponentVersion(ComponentVersionKey componentVersionKey, String description) {
        super(componentVersionKey);
        this.description = description;
        this.builds = new ArrayList<>();
    }

    //Getters and Setters
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ComponentBuild> getBuilds() {
        return builds;
    }

    public void setBuilds(List<ComponentBuild> builds) {
        this.builds = builds;
    }

    public long getNumber() {
        return getMetadataKey().getComponentVersionNumber();
    }

}