package io.metadew.iesi.metadata.configuration.execution.script.exception;

import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;

public class ScriptExecutionRequestAlreadyExistsException extends MetadataAlreadyExistsException {

	private static final long serialVersionUID = 1L;

	public ScriptExecutionRequestAlreadyExistsException(String message) {
        super(message);
    }

}
