package io.metadew.iesi.server.rest.script;

public enum ScriptFilterOption {

    NAME("name"),
    VERSION("version"),
    LABEL("label"),
    INCLUDE_INACTIVE("includeInActive");

    private final String keyword;

    ScriptFilterOption(String keyword) {
        this.keyword = keyword;
    }

    public String keyword() {
        return keyword;
    }

}
