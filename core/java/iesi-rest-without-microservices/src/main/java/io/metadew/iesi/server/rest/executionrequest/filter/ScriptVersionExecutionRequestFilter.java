package io.metadew.iesi.server.rest.executionrequest.filter;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

public class ScriptVersionExecutionRequestFilter extends ExecutionRequestFilter {


    public ScriptVersionExecutionRequestFilter(String value, boolean exactMatch) {
        super(ExecutionRequestFilterOption.VERSION, value, exactMatch);
    }

    public void addParameter(MapSqlParameterSource parameters) {
        parameters.addValue("script_version", Long.parseLong(getValue()));
    }

}
