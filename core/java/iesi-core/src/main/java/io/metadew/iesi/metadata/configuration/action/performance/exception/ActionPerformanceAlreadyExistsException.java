package io.metadew.iesi.metadata.configuration.action.performance.exception;

import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;

public class ActionPerformanceAlreadyExistsException extends MetadataAlreadyExistsException {

	private static final long serialVersionUID = 1L;

	public ActionPerformanceAlreadyExistsException(String message) {
        super(message);
    }

}
