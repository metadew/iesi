package io.metadew.iesi.server.rest.component;

public enum ComponentFilterOption {

    NAME("name"),
    VERSION("version");

    private final String keyword;

    ComponentFilterOption(String keyword) {
        this.keyword = keyword;
    }
}
