package io.metadew.iesi.server.rest.environment;

public enum EnvironmentFilterOption {
    NAME("name");

    private final String keyword;

    EnvironmentFilterOption(String keyword) {
        this.keyword = keyword;
    }
}
