package io.metadew.iesi.metadata.definition;

import java.util.List;

public class Artefact {

    private long id;
    private String type;
    private String name;
    private List<Classification> classifications;

    // Constructors
    public Artefact() {

    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Classification> getClassifications() {
        return classifications;
    }

    public void setClassifications(List<Classification> classifications) {
        this.classifications = classifications;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}