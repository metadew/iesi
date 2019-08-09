package io.metadew.iesi.metadata.configuration.exception.action;

import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;

public class ActionParameterDoesNotExistException extends MetadataDoesNotExistException {

	private static final long serialVersionUID = 1L;

	public ActionParameterDoesNotExistException(String message) {
        super(message);
    }

}
