package io.metadew.iesi.metadata.configuration.exception.script;

import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;

public class ScriptTraceDoesNotExistException extends MetadataDoesNotExistException {

	private static final long serialVersionUID = 1L;

	public ScriptTraceDoesNotExistException(String message) {
        super(message);
    }

}
