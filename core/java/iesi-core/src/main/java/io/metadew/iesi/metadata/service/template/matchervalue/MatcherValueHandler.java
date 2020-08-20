package io.metadew.iesi.metadata.service.template.matchervalue;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.metadata.definition.template.matcher.value.MatcherValue;
import io.metadew.iesi.metadata.service.template.matchervalue.any.AnyMatcherValueService;
import io.metadew.iesi.metadata.service.template.matchervalue.fixed.FixedMatcherValueService;
import io.metadew.iesi.metadata.service.template.matchervalue.template.TemplateMatcherValueService;
import io.metadew.iesi.script.execution.ExecutionRuntime;

import java.util.ArrayList;
import java.util.List;

public class MatcherValueHandler implements IMatcherValueHandler {

    private static MatcherValueHandler INSTANCE;
    private final List<IMatcherValueService> matcherValueServices;

    public synchronized static MatcherValueHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MatcherValueHandler();
        }
        return INSTANCE;
    }

    private MatcherValueHandler() {
        matcherValueServices = new ArrayList<>();
        matcherValueServices.add(AnyMatcherValueService.getInstance());
        matcherValueServices.add(FixedMatcherValueService.getInstance());
        matcherValueServices.add(TemplateMatcherValueService.getInstance());
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean matches(MatcherValue matcherValue, DataType dataType, ExecutionRuntime executionRuntime) {
        return getMatcherValueServices(matcherValue).matches(matcherValue, dataType, executionRuntime);
    }

    private IMatcherValueService getMatcherValueServices(MatcherValue matcherValue) {
        return matcherValueServices.stream()
                .filter(iHttpResponseEntityService -> iHttpResponseEntityService.appliesToClass().equals(matcherValue.getClass()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Unsupported HttpResponseEntityStrategy '" + matcherValue.getClass().getSimpleName() + "' for http response"));
    }

}
