package io.metadew.iesi.openapi;

public class UnsupportedSecurityScheme extends RuntimeException {
    public UnsupportedSecurityScheme(String message) {
        super(message);
    }
}
