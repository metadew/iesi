package io.metadew.iesi.gcp.common.configuration;

public enum Code {
    BQL("bql"),
    PUBSUB("pubsub"),
    TOPIC("topic"),
    SUBSCRIPTION("subscription"),
    DLP("dlp");

    private String value;

    Code(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
