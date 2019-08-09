package io.metadew.iesi.metadata.configuration.exception.script;

import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;

public class ScriptVersionAlreadyExistsException extends MetadataAlreadyExistsException {

	private static final long serialVersionUID = 1L;

	public ScriptVersionAlreadyExistsException(String message) {
        super(message);
    }

}
