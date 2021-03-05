package io.metadew.iesi.openapi;

public class UnsuportedSecurityScheme extends RuntimeException {
    public UnsuportedSecurityScheme(String message) {
        super(message);
    }
}
