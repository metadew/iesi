package io.metadew.iesi.metadata.configuration.action.exception;

import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;

public class ActionAlreadyExistsException extends MetadataAlreadyExistsException {

	private static final long serialVersionUID = 1L;

	public ActionAlreadyExistsException(String message) {
        super(message);
    }

}
