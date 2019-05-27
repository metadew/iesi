package io.metadew.iesi.framework.configuration;

public enum FrameworkKeywords {
    RUNID("runId"),
    PROCESSID("processId");

    private String value;

    FrameworkKeywords(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}