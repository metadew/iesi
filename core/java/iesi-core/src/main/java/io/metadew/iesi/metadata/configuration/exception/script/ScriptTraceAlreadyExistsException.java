package io.metadew.iesi.metadata.configuration.exception.script;

import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;

public class ScriptTraceAlreadyExistsException extends MetadataAlreadyExistsException {

	private static final long serialVersionUID = 1L;

	public ScriptTraceAlreadyExistsException(String message) {
        super(message);
    }

}
