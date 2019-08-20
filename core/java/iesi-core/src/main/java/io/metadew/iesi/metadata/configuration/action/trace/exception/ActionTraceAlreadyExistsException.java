package io.metadew.iesi.metadata.configuration.action.trace.exception;

import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;

public class ActionTraceAlreadyExistsException extends MetadataAlreadyExistsException {

	private static final long serialVersionUID = 1L;

	public ActionTraceAlreadyExistsException(String message) {
        super(message);
    }

}
