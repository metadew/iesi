package io.metadew.iesi.metadata.configuration.action.result.exception;

import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;

public class ActionResultOutputAlreadyExistsException extends MetadataAlreadyExistsException {

	private static final long serialVersionUID = 1L;

	public ActionResultOutputAlreadyExistsException(String message) {
        super(message);
    }

}
