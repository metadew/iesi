package io.metadew.iesi.server.rest.executionrequest.filter;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

public class LabelExecutionRequestFilter extends ExecutionRequestFilter {


    public LabelExecutionRequestFilter(String value, boolean exactMatch) {
        super(ExecutionRequestFilterOption.LABEL, value, exactMatch);
    }

    public void addParameter(MapSqlParameterSource parameters) {
        parameters.addValue("label_key",  isExactMatch() ? getValue() : ("%" + getValue().split(":")[0] + "%"));
        parameters.addValue("label_name",  isExactMatch() ? getValue() : ("%" + getValue().split(":")[1] + "%"));
    }

}
