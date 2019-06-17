package io.metadew.iesi.server.rest.error;

public class DataBadRequestException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public DataBadRequestException(String name) {
        super("Mismatch between url query" + " '" + name + "' and request body " );
    }
}
