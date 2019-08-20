package io.metadew.iesi.metadata.configuration.script.exception;

import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;

public class ScriptAlreadyExistsException extends MetadataAlreadyExistsException {

	private static final long serialVersionUID = 1L;

	public ScriptAlreadyExistsException(String message) {
        super(message);
    }

}
