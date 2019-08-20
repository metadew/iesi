package io.metadew.iesi.metadata.configuration.action.result.exception;

import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;

public class ActionResultAlreadyExistsException extends MetadataAlreadyExistsException {

	private static final long serialVersionUID = 1L;

	public ActionResultAlreadyExistsException(String message) {
        super(message);
    }

}
