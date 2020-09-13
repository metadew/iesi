package io.metadew.iesi.server.rest.executionrequest.filter;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

public class IdExecutionRequestFilter extends ExecutionRequestFilter {


    public IdExecutionRequestFilter(String value, boolean exactMatch) {
        super(ExecutionRequestFilterOption.ID, value, exactMatch);
    }

    public void addParameter(MapSqlParameterSource parameters) {
        parameters.addValue("request_id", isExactMatch() ? getValue() : ("%" + getValue() + "%"));
    }

}
