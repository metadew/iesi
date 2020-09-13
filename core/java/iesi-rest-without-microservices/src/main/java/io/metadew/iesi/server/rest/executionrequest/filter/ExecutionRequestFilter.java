package io.metadew.iesi.server.rest.executionrequest.filter;

import lombok.Data;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.util.Map;

@Data
public abstract class ExecutionRequestFilter {

    private final ExecutionRequestFilterOption executionRequestFilterOption;
    private final String value;
    private final boolean exactMatch;

    public abstract void addParameter(MapSqlParameterSource parameters);

}
