package io.metadew.iesi.metadata.service.template;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.dataset.Dataset;
import io.metadew.iesi.datatypes.dataset.DatasetHandler;
import io.metadew.iesi.metadata.configuration.template.TemplateConfiguration;
import io.metadew.iesi.metadata.definition.template.Template;
import io.metadew.iesi.metadata.definition.template.TemplateKey;
import io.metadew.iesi.metadata.definition.template.matcher.Matcher;
import io.metadew.iesi.metadata.service.template.matchervalue.MatcherValueHandler;
import io.metadew.iesi.script.execution.ExecutionRuntime;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

public class TemplateService implements ITemplateService {

    private static TemplateService INSTANCE;

    public synchronized static TemplateService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TemplateService();
        }
        return INSTANCE;
    }

    private TemplateService() {
    }

    public List<Template> getAll() {
        return TemplateConfiguration.getInstance().getAll();
    }

    public boolean exists(TemplateKey templateKey) {
        return TemplateConfiguration.getInstance().exists(templateKey);
    }

    public boolean exists(String templateName) {
        return TemplateConfiguration.getInstance().exists(templateName);
    }

    public void addUser(Template template) {
        TemplateConfiguration.getInstance().insert(template);
    }

    public Optional<Template> get(TemplateKey templateKey) {
        return TemplateConfiguration.getInstance().get(templateKey);
    }

    public Optional<Template> get(String templateName) {
        return TemplateConfiguration.getInstance().getByName(templateName);
    }

    public void update(Template template) {
        TemplateConfiguration.getInstance().update(template);
    }

    public void delete(TemplateKey templateKey) {
        TemplateConfiguration.getInstance().delete(templateKey);
    }

    public void delete(String templateName) {
        TemplateConfiguration.getInstance().deleteByName(templateName);
    }

    @Override
    public boolean matches(DataType dataType, Template template, ExecutionRuntime executionRuntime) {
        if (dataType instanceof Dataset) {
            for (Matcher matcher : template.getMatchers()) {
                if (!DatasetHandler.getInstance().getDataItem((Dataset) dataType, matcher.getKey(), executionRuntime)
                        .map(dataType1 -> MatcherValueHandler.getInstance().matches(matcher.getMatcherValue(), dataType1, executionRuntime))
                        .orElse(false)) {
                    return false;
                }
            }
            return true;
        } else {
            throw new RuntimeException(MessageFormat.format("Cannot compare {0} to {1}", dataType, template));
        }
    }

}
