package io.metadew.iesi.metadata.configuration.exception.action;

import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;

public class ActionParameterDesignTraceDoesNotExistException extends MetadataDoesNotExistException {

	private static final long serialVersionUID = 1L;

	public ActionParameterDesignTraceDoesNotExistException(String message) {
        super(message);
    }

}
