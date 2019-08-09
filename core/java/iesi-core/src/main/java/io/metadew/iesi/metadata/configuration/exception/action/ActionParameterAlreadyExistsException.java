package io.metadew.iesi.metadata.configuration.exception.action;

import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;

public class ActionParameterAlreadyExistsException extends MetadataAlreadyExistsException {

	private static final long serialVersionUID = 1L;

	public ActionParameterAlreadyExistsException(String message) {
        super(message);
    }

}
