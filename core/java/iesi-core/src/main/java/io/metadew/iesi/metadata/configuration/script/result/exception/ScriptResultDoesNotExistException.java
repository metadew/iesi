package io.metadew.iesi.metadata.configuration.script.result.exception;

import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;

public class ScriptResultDoesNotExistException extends MetadataDoesNotExistException {

	private static final long serialVersionUID = 1L;

	public ScriptResultDoesNotExistException(String message) {
        super(message);
    }

}
