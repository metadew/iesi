package io.metadew.iesi.metadata.configuration.script.trace.exception;

import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;

public class ScriptParameterTraceDoesNotExistException extends MetadataDoesNotExistException {

	private static final long serialVersionUID = 1L;

	public ScriptParameterTraceDoesNotExistException(String message) {
        super(message);
    }

}
