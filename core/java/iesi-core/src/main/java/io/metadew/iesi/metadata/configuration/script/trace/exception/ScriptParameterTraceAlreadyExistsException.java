package io.metadew.iesi.metadata.configuration.script.trace.exception;

import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;

public class ScriptParameterTraceAlreadyExistsException extends MetadataAlreadyExistsException {

	private static final long serialVersionUID = 1L;

	public ScriptParameterTraceAlreadyExistsException(String message) {
        super(message);
    }

}
