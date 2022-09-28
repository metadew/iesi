package io.metadew.iesi.metadata.configuration.exception;

import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.key.MetadataKey;

import java.text.MessageFormat;

public class MetadataAlreadyExistsException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public MetadataAlreadyExistsException(MetadataKey metadataKey) {
        super(MessageFormat.format("{0}: {1} already exists", metadataKey.getClass().getSimpleName(), metadataKey));
    }

    public MetadataAlreadyExistsException(Metadata<?> metadata) {
        this(metadata.getMetadataKey());
    }

    public MetadataAlreadyExistsException(String metadataName) {
        super(MessageFormat.format("{0} already exists", metadataName));
    }

}
