package io.metadew.iesi.metadata.configuration.script.exception;

import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;

public class ScriptDoesNotExistException extends MetadataDoesNotExistException {

	private static final long serialVersionUID = 1L;

	public ScriptDoesNotExistException(String message) {
        super(message);
    }

}
