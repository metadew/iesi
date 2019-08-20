package io.metadew.iesi.metadata.configuration.script.design.exception;

import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;

public class ScriptParameterDesignTraceDoesNotExistException extends MetadataDoesNotExistException {

	private static final long serialVersionUID = 1L;

	public ScriptParameterDesignTraceDoesNotExistException(String message) {
        super(message);
    }

}
