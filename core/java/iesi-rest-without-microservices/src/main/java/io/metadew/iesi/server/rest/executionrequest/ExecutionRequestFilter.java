package io.metadew.iesi.server.rest.executionrequest;

import lombok.Data;

@Data
public class ExecutionRequestFilter {

    private final ExecutionRequestFilterOption executionRequestFilterOption;
    private final String value;
    private final boolean exactMatch;

}
