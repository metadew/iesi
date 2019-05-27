package io.metadew.iesi.metadata.definition;

public class MetadataRepository {

    private String name;
    private String type = "script";
    private String instance;

    // Constructors
    public MetadataRepository() {

    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }

}