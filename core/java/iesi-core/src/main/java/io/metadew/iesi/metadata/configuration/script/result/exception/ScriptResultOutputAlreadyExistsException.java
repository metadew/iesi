package io.metadew.iesi.metadata.configuration.script.result.exception;

import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;

public class ScriptResultOutputAlreadyExistsException extends MetadataAlreadyExistsException {

	private static final long serialVersionUID = 1L;

	public ScriptResultOutputAlreadyExistsException(String message) {
        super(message);
    }

}
