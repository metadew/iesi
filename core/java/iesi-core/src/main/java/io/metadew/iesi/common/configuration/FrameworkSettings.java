package io.metadew.iesi.common.configuration;

public enum FrameworkSettings {
    VERSION("v0.1.0");

    private String value;

    FrameworkSettings(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}