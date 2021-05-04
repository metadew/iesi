package io.metadew.iesi.server.rest.connection;

public enum ConnectionFilterOption {
    NAME("name"),
    VERSION("version");

    private final String keyword;

    ConnectionFilterOption(String keyword) {
        this.keyword = keyword;
    }
}
