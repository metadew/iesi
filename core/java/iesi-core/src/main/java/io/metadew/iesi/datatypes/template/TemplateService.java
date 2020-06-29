package io.metadew.iesi.datatypes.template;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.DataTypeHandler;
import io.metadew.iesi.datatypes.IDataTypeService;
import io.metadew.iesi.datatypes.dataset.Dataset;
import io.metadew.iesi.datatypes.dataset.DatasetHandler;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.configuration.template.TemplateConfiguration;
import io.metadew.iesi.metadata.definition.template.Template;
import io.metadew.iesi.metadata.definition.template.TemplateKey;
import io.metadew.iesi.metadata.definition.template.matcher.Matcher;
import io.metadew.iesi.metadata.definition.template.matcher.value.MatcherAnyValue;
import io.metadew.iesi.metadata.definition.template.matcher.value.MatcherFixedValue;
import io.metadew.iesi.metadata.definition.template.matcher.value.MatcherTemplate;
import io.metadew.iesi.metadata.definition.template.matcher.value.MatcherValue;
import io.metadew.iesi.metadata.service.template.ITemplateService;
import io.metadew.iesi.metadata.service.template.matchervalue.MatcherValueHandler;
import io.metadew.iesi.script.execution.ExecutionRuntime;
import lombok.extern.log4j.Log4j2;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
public class TemplateService implements IDataTypeService<Template>, ITemplateService {

    private static TemplateService INSTANCE;

    public synchronized static TemplateService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TemplateService();
        }
        return INSTANCE;
    }

    private TemplateService() {
    }

    @Override
    public Class<Template> appliesTo() {
        return Template.class;
    }

    @Override
    public String keyword() {
        return "template";
    }

    @Override
    public Template resolve(String input, ExecutionRuntime executionRuntime) {
        log.trace(MessageFormat.format("resolving {0} for Template", input));
        input = executionRuntime.resolveVariables(input);
        List<String> splittedArguments = DataTypeHandler.getInstance().splitInstructionArguments(input);
        if (splittedArguments.size() == 2) {
            DataType templateName = DataTypeHandler.getInstance().resolve(splittedArguments.get(0), executionRuntime);
            DataType templateVersion = DataTypeHandler.getInstance().resolve(splittedArguments.get(1), executionRuntime);
            if (templateName instanceof Text && templateVersion instanceof Text) {
                return TemplateConfiguration.getInstance()
                        .getByNameAndVersion(((Text) templateName).getString(), Long.parseLong(((Text) templateVersion).getString()))
                        .orElseThrow(() -> new RuntimeException("Cannot find template with name " + ((Text) templateName).getString()));
            } else {
                throw new IllegalArgumentException("Cannot fetch template when given " + templateName);
            }
        } else {
            throw new IllegalArgumentException("Cannot fetch template with parameters " + input);
        }
    }

    @Override
    public boolean equals(Template _this, Template other, ExecutionRuntime executionRuntime) {
        if (_this == null && other == null) {
            return true;
        }
        if (_this == null || other == null) {
            return false;
        }
        List<Matcher> thisMatchers = _this.getMatchers();
        List<Matcher> otherMatchers = other.getMatchers();

        if (!thisMatchers.stream()
                .map(Matcher::getKey)
                .collect(Collectors.toSet())
                .equals(otherMatchers.stream()
                        .map(Matcher::getKey)
                        .collect(Collectors.toSet()))) {
            return false;
        }

        for (Matcher thisMatcher : thisMatchers) {
            if (!otherMatchers.stream()
                    .filter(matcher -> matcher.getKey().equals(thisMatcher.getKey()))
                    .findFirst()
                    .map(matcher -> equals(matcher.getMatcherValue(), thisMatcher.getMatcherValue(), executionRuntime))
                    .orElse(false)) {
                return false;
            }
        }
        return false;
    }

    private boolean equals(MatcherValue _this, MatcherValue other, ExecutionRuntime executionRuntime) {
        if (_this == null && other == null) {
            return true;
        }
        if (_this == null || other == null) {
            return false;
        }
        if (!_this.getClass().equals(other.getClass())) {
            return false;
        }
        if (_this instanceof MatcherAnyValue) {
            return true;
        } else if (_this instanceof MatcherFixedValue) {
            return DataTypeHandler.getInstance().equals(
                    DataTypeHandler.getInstance().resolve(((MatcherFixedValue) _this).getValue(), executionRuntime),
                    DataTypeHandler.getInstance().resolve(((MatcherFixedValue) other).getValue(), executionRuntime),
                    executionRuntime);
        } else if (_this instanceof MatcherTemplate) {
            return ((MatcherTemplate) _this).getTemplateName().equals(((MatcherTemplate) other).getTemplateName()) &&
                    ((MatcherTemplate) _this).getTemplateVersion().equals(((MatcherTemplate) other).getTemplateVersion());
        } else {
            return false;
        }
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

    public Optional<Template> get(String templatename, long version) {
        return TemplateConfiguration.getInstance().getByNameAndVersion(templatename, version);
    }

    public void update(Template template) {
        TemplateConfiguration.getInstance().update(template);
    }

    public void delete(TemplateKey templateKey) {
        TemplateConfiguration.getInstance().delete(templateKey);
    }

    public void delete(String name, long version) {
        TemplateConfiguration.getInstance().deleteByNameAndVersion(name, version);
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
