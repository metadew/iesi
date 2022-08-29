package io.metadew.iesi.server.rest.user.team;

public enum TeamFilterOption {

    NAME("name");

    private final String keyword;

    TeamFilterOption(String keyword) {
        this.keyword = keyword;
    }
}
