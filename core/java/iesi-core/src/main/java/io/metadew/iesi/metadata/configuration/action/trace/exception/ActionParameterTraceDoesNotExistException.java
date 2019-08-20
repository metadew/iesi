package io.metadew.iesi.metadata.configuration.action.trace.exception;

import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;

public class ActionParameterTraceDoesNotExistException extends MetadataDoesNotExistException {

	private static final long serialVersionUID = 1L;

	public ActionParameterTraceDoesNotExistException(String message) {
        super(message);
    }

}
