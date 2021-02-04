package io.metadew.iesi.datatypes.dataset.implementation;

public enum DatasetImplementationType {
    IN_MEMORY("in_memory");

    private final String label;

    DatasetImplementationType(String label) {
        this.label = label;
    }

    public String value() {
        return label;
    }
}
