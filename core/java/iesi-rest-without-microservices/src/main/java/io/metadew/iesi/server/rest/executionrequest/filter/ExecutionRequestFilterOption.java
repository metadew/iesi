package io.metadew.iesi.server.rest.executionrequest.filter;

public enum ExecutionRequestFilterOption {

    NAME("script"),
    VERSION("version"),
    LABEL("label"),
    ENVIRONMENT("environment"),
    ID("id"),
    IDS("ids");

    private final String keyword;

    ExecutionRequestFilterOption(String keyword) {
        this.keyword = keyword;
    }

    public String keyword() {
        return keyword;
    }

}
