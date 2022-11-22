package io.metadew.iesi.metadata.configuration.exception;

import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.key.MetadataKey;

import java.text.MessageFormat;

public class MetadataDoesNotExistException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public MetadataDoesNotExistException(MetadataKey metadataKey) {
        super(MessageFormat.format("{0}: {1} does not exists", metadataKey.getClass().getSimpleName(), metadataKey.toString()));
    }

    public MetadataDoesNotExistException(Metadata metadata) {
        this(metadata.getMetadataKey());
    }

    public MetadataDoesNotExistException(String message) {
        super(message);
    }

}
