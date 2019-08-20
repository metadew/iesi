package io.metadew.iesi.metadata.configuration.action.result.exception;

import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;

public class ActionResultOutputDoesNotExistException extends MetadataDoesNotExistException {

	private static final long serialVersionUID = 1L;

	public ActionResultOutputDoesNotExistException(String message) {
        super(message);
    }

}
