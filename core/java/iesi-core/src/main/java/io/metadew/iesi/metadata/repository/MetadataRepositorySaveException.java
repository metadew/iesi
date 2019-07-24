package io.metadew.iesi.metadata.repository;

public class MetadataRepositorySaveException extends Exception {

    public MetadataRepositorySaveException(Exception exception) {
        super(exception);
    }

    public MetadataRepositorySaveException(String message) {
        super(message);
    }

    public MetadataRepositorySaveException() {
        super();
    }
}
