package io.metadew.iesi.metadata.configuration.script.design.exception;

import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;

public class ScriptVersionDesignTraceDoesNotExistException extends MetadataDoesNotExistException {

	private static final long serialVersionUID = 1L;

	public ScriptVersionDesignTraceDoesNotExistException(String message) {
        super(message);
    }

}
