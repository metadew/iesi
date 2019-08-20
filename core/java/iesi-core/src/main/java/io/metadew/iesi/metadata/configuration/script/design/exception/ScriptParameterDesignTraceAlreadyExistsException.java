package io.metadew.iesi.metadata.configuration.script.design.exception;

import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;

public class ScriptParameterDesignTraceAlreadyExistsException extends MetadataAlreadyExistsException {

	private static final long serialVersionUID = 1L;

	public ScriptParameterDesignTraceAlreadyExistsException(String message) {
        super(message);
    }

}
