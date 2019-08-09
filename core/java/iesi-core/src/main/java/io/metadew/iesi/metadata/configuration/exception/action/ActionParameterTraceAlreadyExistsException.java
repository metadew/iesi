package io.metadew.iesi.metadata.configuration.exception.action;

import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;

public class ActionParameterTraceAlreadyExistsException extends MetadataAlreadyExistsException {

	private static final long serialVersionUID = 1L;

	public ActionParameterTraceAlreadyExistsException(String message) {
        super(message);
    }

}
