package io.metadew.iesi.metadata.configuration.script.trace.exception;

import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;

public class ScriptVersionTraceAlreadyExistsException extends MetadataAlreadyExistsException {

	private static final long serialVersionUID = 1L;

	public ScriptVersionTraceAlreadyExistsException(String message) {
        super(message);
    }

}
