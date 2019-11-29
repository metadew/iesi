package io.metadew.iesi.metadata.configuration.connection.exception;

import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;

public class ConnectionDoesNotExistException extends MetadataDoesNotExistException {

    private static final long serialVersionUID = 1L;

    public ConnectionDoesNotExistException(String message) {
        super(message);
    }
}
