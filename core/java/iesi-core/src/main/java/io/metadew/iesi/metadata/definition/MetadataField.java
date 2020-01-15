package io.metadew.iesi.metadata.definition;

import java.util.Optional;

public class MetadataField {

    private String name;
    private String description;
    private int order;
    private String type;
    private int length;
    private String nullable = "Y";
    private String defaultTimestamp = "N";
    private boolean primaryKey = false;

    //Constructors
    public MetadataField() {

    }

    public MetadataField(String name, String description, int order, String type, int length, String nullable, String defaultTimestamp, boolean primaryKey) {
        this.name = name;
        this.description = description;
        this.order = order;
        this.type = type;
        this.length = length;
        this.nullable = nullable;
        this.defaultTimestamp = defaultTimestamp;
        this.primaryKey = primaryKey;
    }

    //Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
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

    public String getNullable() {
        return nullable;
    }

    public void setNullable(String nullable) {
        this.nullable = nullable;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getDefaultTimestamp() {
        return defaultTimestamp;
    }

    public void setDefaultTimestamp(String defaultTimestamp) {
        this.defaultTimestamp = defaultTimestamp;
    }


    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }
}