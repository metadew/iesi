package io.metadew.iesi.metadata.configuration.execution.exception;

import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;

public class ExecutionRequestDoesNotExistException extends MetadataDoesNotExistException {

	private static final long serialVersionUID = 1L;

	public ExecutionRequestDoesNotExistException(String message) {
        super(message);
    }

}
