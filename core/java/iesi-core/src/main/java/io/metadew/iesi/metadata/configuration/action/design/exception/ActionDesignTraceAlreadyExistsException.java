package io.metadew.iesi.metadata.configuration.action.design.exception;

import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;

public class ActionDesignTraceAlreadyExistsException extends MetadataAlreadyExistsException {

	private static final long serialVersionUID = 1L;

	public ActionDesignTraceAlreadyExistsException(String message) {
        super(message);
    }

}
