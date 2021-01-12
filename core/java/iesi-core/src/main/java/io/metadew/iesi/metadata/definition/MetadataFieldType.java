package io.metadew.iesi.metadata.definition;

public enum MetadataFieldType {
    STRING("string"),
    FLAG("flag"),
    NUMBER("number"),
    TIMESTAMP("timestamp"),
    CLOB("clob");

    private final String label;

    MetadataFieldType(String label) {
        this.label = label;
    }

    public String value() {
        return label;
    }

}