package io.metadew.iesi.server.rest.executionrequest.filter;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

public class EnvironmentExecutionRequestFilter extends ExecutionRequestFilter {


    public EnvironmentExecutionRequestFilter(String value, boolean exactMatch) {
        super(ExecutionRequestFilterOption.ENVIRONMENT, value, exactMatch);
    }

    public void addParameter(MapSqlParameterSource parameters) {
        parameters.addValue("environment", isExactMatch() ? getValue() : ("%" + getValue() + "%"));
    }

}
