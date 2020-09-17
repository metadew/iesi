package io.metadew.iesi.gcp.connection.bigquery;

public class BigqueryField {

    private String description;
    private String mode;
    private String name;
    private String type;

    public BigqueryField(String name, String type, String mode, String description) {
        this.setName(name);
        this.setType(type);
        this.setMode(mode);
        this.setDescription(description);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

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
}
