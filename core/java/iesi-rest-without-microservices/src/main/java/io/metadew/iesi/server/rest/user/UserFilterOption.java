package io.metadew.iesi.server.rest.user;

public enum UserFilterOption {

    USERNAME("username");

    private final String keyword;

    UserFilterOption(String keyword) {
        this.keyword = keyword;
    }

    public String keyword() {
        return keyword;
    }

}
