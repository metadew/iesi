package io.metadew.iesi.gcp.bqloader.common.configuration;

public enum Settings {
    CODE("bql");

    private String value;

    Settings(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
