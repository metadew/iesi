package io.metadew.iesi.metadata.configuration.script.result.exception;

import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;

public class ScriptResultAlreadyExistsException extends MetadataAlreadyExistsException {

	private static final long serialVersionUID = 1L;

	public ScriptResultAlreadyExistsException(String message) {
        super(message);
    }

}
