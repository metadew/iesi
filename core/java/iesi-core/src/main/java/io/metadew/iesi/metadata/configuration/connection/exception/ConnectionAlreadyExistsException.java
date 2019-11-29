package io.metadew.iesi.metadata.configuration.connection.exception;

import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;

public class ConnectionAlreadyExistsException extends MetadataAlreadyExistsException {

    public ConnectionAlreadyExistsException(String message){
        super(message);
    }
}
