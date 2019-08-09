package io.metadew.iesi.metadata.configuration.exception.script;

import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;

public class ScriptVersionDoesNotExistException extends MetadataDoesNotExistException {

	private static final long serialVersionUID = 1L;

	public ScriptVersionDoesNotExistException(String message) {
        super(message);
    }

}
