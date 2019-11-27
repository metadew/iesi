package io.metadew.iesi.connection.r;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.Reader;
import java.util.Optional;

public class RCommandResult {

    private Integer statusCode;
    private String output;

    public RCommandResult(int statusCode, String output) {
        this.statusCode = statusCode;
        this.output = output;
    }

    public RCommandResult() {
        this.statusCode = null;
        this.output = null;
    }

    public RCommandResult(int statusCode, Reader output) throws IOException {
        this.statusCode = statusCode;
        this.output = IOUtils.toString(output);
    }


    public Optional<String> getOutput() {
        return Optional.ofNullable(output);
    }

    public Optional<Integer> getStatusCode() {
        return Optional.ofNullable(statusCode);
    }
}
