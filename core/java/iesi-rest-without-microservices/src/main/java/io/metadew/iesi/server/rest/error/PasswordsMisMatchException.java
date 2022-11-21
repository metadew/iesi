package io.metadew.iesi.server.rest.error;

public class PasswordsMisMatchException extends RuntimeException {

    public PasswordsMisMatchException() {
        super("The provided passwords do not match each other");
    }
}
