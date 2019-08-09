package io.metadew.iesi.metadata.configuration.exception.action;

import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;

public class ActionDoesNotExistException extends MetadataDoesNotExistException {

	private static final long serialVersionUID = 1L;

	public ActionDoesNotExistException(String message) {
        super(message);
    }

}
