package io.metadew.iesi.server.rest.executionrequest;

public enum ExecutionRequestFilterOption {

    NAME("script"),
    VERSION("version"),
    LABEL("label"),
    ENVIRONMENT("environment"),
    ID("id");

    private final String keyword;

    ExecutionRequestFilterOption(String keyword) {
        this.keyword = keyword;
    }

    public String keyword() {
        return keyword;
    }

}
