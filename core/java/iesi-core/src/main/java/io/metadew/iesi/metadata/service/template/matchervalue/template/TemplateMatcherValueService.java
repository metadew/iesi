package io.metadew.iesi.metadata.service.template.matchervalue.template;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.template.TemplateService;
import io.metadew.iesi.metadata.definition.template.matcher.value.MatcherTemplate;
import io.metadew.iesi.metadata.service.template.matchervalue.IMatcherValueService;
import io.metadew.iesi.script.execution.ExecutionRuntime;

public class TemplateMatcherValueService implements IMatcherValueService<MatcherTemplate> {

    private static TemplateMatcherValueService INSTANCE;

    public synchronized static TemplateMatcherValueService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TemplateMatcherValueService();
        }
        return INSTANCE;
    }

    @Override
    public boolean matches(MatcherTemplate matcherValue, DataType dataType, ExecutionRuntime executionRuntime) {
        return TemplateService.getInstance()
                .matches(dataType,
                        TemplateService.getInstance()
                                .get(matcherValue.getTemplateName(), matcherValue.getTemplateVersion())
                                .orElseThrow(() -> new RuntimeException("Could not find template " + matcherValue.getTemplateName() + "-" + matcherValue.getTemplateVersion())),
                        executionRuntime);
    }

    @Override
    public Class<MatcherTemplate> appliesToClass() {
        return MatcherTemplate.class;
    }
}
