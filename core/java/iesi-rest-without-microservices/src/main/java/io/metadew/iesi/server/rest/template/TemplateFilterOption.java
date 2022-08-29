package io.metadew.iesi.server.rest.template;

public enum TemplateFilterOption {
    NAME("name"),
    VERSION("version");

    private final String keyword;

    TemplateFilterOption(String keyword) {
        this.keyword = keyword;
    }

    public String keyword() {
        return keyword;
    }
}
