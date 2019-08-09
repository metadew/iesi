package io.metadew.iesi.metadata.configuration.exception.action;

import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;

public class ActionTraceDoesNotExistException extends MetadataDoesNotExistException {

	private static final long serialVersionUID = 1L;

	public ActionTraceDoesNotExistException(String message) {
        super(message);
    }

}
