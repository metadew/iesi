package io.metadew.iesi.metadata.configuration.exception;

import io.metadew.iesi.metadata.definition.key.MetadataKey;

import java.text.MessageFormat;

public class MetadataAlreadyExistsException extends Exception {

	private static final long serialVersionUID = 1L;

	public MetadataAlreadyExistsException(String message) {
        super(message);
    }

    public MetadataAlreadyExistsException(String className, MetadataKey metadataKey){
        this(MessageFormat.format("{0}: {1} already exists", className, metadataKey));
    }

}
