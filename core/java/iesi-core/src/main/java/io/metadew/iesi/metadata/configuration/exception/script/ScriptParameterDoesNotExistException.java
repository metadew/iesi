package io.metadew.iesi.metadata.configuration.exception.script;

import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;

public class ScriptParameterDoesNotExistException extends MetadataDoesNotExistException {

	private static final long serialVersionUID = 1L;

	public ScriptParameterDoesNotExistException(String message) {
        super(message);
    }

}
