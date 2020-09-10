package io.metadew.iesi.metadata.service.template.matchervalue;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.metadata.definition.template.matcher.value.MatcherValue;
import io.metadew.iesi.script.execution.ExecutionRuntime;

public interface IMatcherValueHandler {

    public boolean matches(MatcherValue matcherValue, DataType dataType, ExecutionRuntime executionRuntime);

}
