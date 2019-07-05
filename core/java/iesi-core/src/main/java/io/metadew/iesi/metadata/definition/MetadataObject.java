package io.metadew.iesi.metadata.definition;

public class MetadataObject {

    private String name;
    private String label;
    private String description;
    private String type = "";
    private String category = "";
    private String migrate = "N";
    private String migrationKey = "A000";

    //Constructors
    public MetadataObject() {

    }

    //Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMigrate() {
        return migrate;
    }

    public void setMigrate(String migrate) {
        this.migrate = migrate;
    }

    public String getMigrationKey() {
        return migrationKey;
    }

    public void setMigrationKey(String migrationKey) {
        this.migrationKey = migrationKey;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }


}