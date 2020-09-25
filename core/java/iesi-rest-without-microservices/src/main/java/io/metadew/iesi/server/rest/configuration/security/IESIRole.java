package io.metadew.iesi.server.rest.configuration.security;

public enum IESIRole {
    ADMIN("ADMIN"),
    TECHNICAL_ENGINEER("TECHNICAL_ENGINEER"),
    TEST_ENGINEER("TEST_ENGINEER"),
    EXECUTOR("EXECUTOR"),
    VIEWER("VIEWER");

    public final String label;

    private IESIRole(String label) {
        this.label = label;
    }

}
