package io.metadew.iesi.metadata.definition;

import java.util.ArrayList;
import java.util.List;

public class ComponentVersion {

    private long number;
    private String description;
    private List<ComponentBuild> builds;

    //Constructors
    public ComponentVersion() {

    }

    public ComponentVersion(long number, String description) {
        this.number = number;
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
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

}