package io.metadew.iesi.metadata.configuration.action.exception;

import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;

public class ActionParameterDoesNotExistException extends MetadataDoesNotExistException {

	private static final long serialVersionUID = 1L;

	public ActionParameterDoesNotExistException(String message) {
        super(message);
    }

}
