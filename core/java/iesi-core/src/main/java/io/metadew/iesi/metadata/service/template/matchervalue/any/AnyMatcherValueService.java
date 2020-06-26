package io.metadew.iesi.metadata.service.template.matchervalue.any;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.metadata.definition.template.matcher.value.MatcherAnyValue;
import io.metadew.iesi.metadata.service.template.matchervalue.IMatcherValueService;
import io.metadew.iesi.script.execution.ExecutionRuntime;

public class AnyMatcherValueService implements IMatcherValueService<MatcherAnyValue> {

    private static AnyMatcherValueService INSTANCE;

    public synchronized static AnyMatcherValueService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AnyMatcherValueService();
        }
        return INSTANCE;
    }

    @Override
    public boolean matches(MatcherAnyValue matcherValue, DataType dataType, ExecutionRuntime executionRuntime) {
        return true;
    }

    @Override
    public Class<MatcherAnyValue> appliesToClass() {
        return MatcherAnyValue.class;
    }
}
