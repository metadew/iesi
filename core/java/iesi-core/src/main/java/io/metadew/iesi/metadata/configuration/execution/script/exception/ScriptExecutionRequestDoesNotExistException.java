package io.metadew.iesi.metadata.configuration.execution.script.exception;

import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;

public class ScriptExecutionRequestDoesNotExistException extends MetadataDoesNotExistException {

	private static final long serialVersionUID = 1L;

	public ScriptExecutionRequestDoesNotExistException(String message) {
        super(message);
    }

}
