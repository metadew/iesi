package io.metadew.iesi.metadata.configuration.action.result.exception;

import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;

public class ActionResultDoesNotExistException extends MetadataDoesNotExistException {

	private static final long serialVersionUID = 1L;

	public ActionResultDoesNotExistException(String message) {
        super(message);
    }

}
