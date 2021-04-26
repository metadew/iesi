package io.metadew.iesi.server.rest.component;

public enum ComponentFilterOption {

    NAME("name");

    private final String keyword;

    ComponentFilterOption(String keyword) {
        this.keyword = keyword;
    }
}
