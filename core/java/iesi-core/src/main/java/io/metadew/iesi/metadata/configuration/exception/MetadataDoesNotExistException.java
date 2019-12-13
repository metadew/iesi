package io.metadew.iesi.metadata.configuration.exception;

import io.metadew.iesi.metadata.definition.key.MetadataKey;

import java.text.MessageFormat;

public class MetadataDoesNotExistException extends Exception {

	private static final long serialVersionUID = 1L;

	public MetadataDoesNotExistException(String message) {
        super(message);
    }

    public MetadataDoesNotExistException(String className, MetadataKey metadataKey){
        this(MessageFormat.format(
                "{0}: {1} does not exists", className, metadataKey.toString()));
    }

}
