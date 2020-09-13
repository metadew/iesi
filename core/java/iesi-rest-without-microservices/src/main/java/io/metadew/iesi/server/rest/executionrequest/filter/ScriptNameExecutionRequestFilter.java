package io.metadew.iesi.server.rest.executionrequest.filter;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

public class ScriptNameExecutionRequestFilter extends ExecutionRequestFilter {


    public ScriptNameExecutionRequestFilter(String value, boolean exactMatch) {
        super(ExecutionRequestFilterOption.NAME, value, exactMatch);
    }

    public void addParameter(MapSqlParameterSource parameters) {
        parameters.addValue("script_name", isExactMatch() ? getValue() : ("%" + getValue() + "%"));
    }

}
