package io.metadew.iesi.metadata.configuration.execution.exception;

import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;

public class ExecutionRequestAlreadyExistsException extends MetadataAlreadyExistsException {

	private static final long serialVersionUID = 1L;

	public ExecutionRequestAlreadyExistsException(String message) {
        super(message);
    }

}
