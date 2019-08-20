package io.metadew.iesi.metadata.configuration.action.design.exception;

import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;

public class ActionDesignTraceDoesNotExistException extends MetadataDoesNotExistException {

	private static final long serialVersionUID = 1L;

	public ActionDesignTraceDoesNotExistException(String message) {
        super(message);
    }

}
