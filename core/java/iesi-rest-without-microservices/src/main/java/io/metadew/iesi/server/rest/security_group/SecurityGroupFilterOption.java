package io.metadew.iesi.server.rest.security_group;

public enum SecurityGroupFilterOption {
    NAME("name");

    private final String keyword;

    SecurityGroupFilterOption(String keyword) {
        this.keyword = keyword;
    }

    public String keyword() {
        return keyword;
    }
}
