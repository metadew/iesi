package io.metadew.iesi.server.rest.dataset;

public enum DatasetFilterOption {

    NAME("name");

    private final String keyword;

    DatasetFilterOption(String keyword) {
        this.keyword = keyword;
    }

    public String keyword() {
        return keyword;
    }

}
