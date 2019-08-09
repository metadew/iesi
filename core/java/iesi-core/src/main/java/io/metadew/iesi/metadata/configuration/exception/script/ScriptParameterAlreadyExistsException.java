package io.metadew.iesi.metadata.configuration.exception.script;

import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;

public class ScriptParameterAlreadyExistsException extends MetadataAlreadyExistsException {

	private static final long serialVersionUID = 1L;

	public ScriptParameterAlreadyExistsException(String message) {
        super(message);
    }

}
