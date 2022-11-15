package io.metadew.iesi.datatypes.template;

import io.metadew.iesi.SpringContext;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.DataTypeHandler;
import io.metadew.iesi.datatypes.IDataTypeService;
import io.metadew.iesi.datatypes.dataset.implementation.inmemory.InMemoryDatasetImplementation;
import io.metadew.iesi.datatypes.dataset.implementation.inmemory.InMemoryDatasetImplementationService;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
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
        List<String> splittedArguments = SpringContext.getBean(DataTypeHandler.class).splitInstructionArguments(input);
        if (splittedArguments.size() == 2) {
            DataType templateName = SpringContext.getBean(DataTypeHandler.class).resolve(splittedArguments.get(0), executionRuntime);
            DataType templateVersion = SpringContext.getBean(DataTypeHandler.class).resolve(splittedArguments.get(1), executionRuntime);
            if (templateName instanceof Text && templateVersion instanceof Text) {
                return SpringContext.getBean(TemplateConfiguration.class)
                        .getByNameAndVersion(((Text) templateName).getString(), Long.parseLong(((Text) templateVersion).getString()))
                        .orElseThrow(() -> new RuntimeException("Cannot find template with name " + ((Text) templateName).getString()));
            } else {
                throw new IllegalArgumentException("Cannot fetch template when given " + templateName);
            }
        } else {
            throw new IllegalArgumentException("Cannot fetch template with parameters " + input);
        }
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
            return SpringContext.getBean(DataTypeHandler.class).equals(
                    SpringContext.getBean(DataTypeHandler.class).resolve(((MatcherFixedValue) _this).getValue(), executionRuntime),
                    SpringContext.getBean(DataTypeHandler.class).resolve(((MatcherFixedValue) other).getValue(), executionRuntime),
                    executionRuntime);
        } else if (_this instanceof MatcherTemplate) {
            return ((MatcherTemplate) _this).getTemplateName().equals(((MatcherTemplate) other).getTemplateName()) &&
                    ((MatcherTemplate) _this).getTemplateVersion().equals(((MatcherTemplate) other).getTemplateVersion());
        } else {
            return false;
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

        if (!thisMatchers.parallelStream()
                .map(Matcher::getKey)
                .collect(Collectors.toSet())
                .equals(otherMatchers.parallelStream()
                        .map(Matcher::getKey)
                        .collect(Collectors.toSet()))) {
            return false;
        }

        return thisMatchers.parallelStream()
                .allMatch(thisMatcher -> otherMatchers.parallelStream()
                        .anyMatch(otherMatcher -> otherMatcher.getKey().equals(thisMatcher.getKey())
                                && equals(thisMatcher.getMatcherValue(), otherMatcher.getMatcherValue(), executionRuntime)));
    }

    public List<Template> getAll() {
        return SpringContext.getBean(TemplateConfiguration.class).getAll();
    }

    public boolean exists(TemplateKey templateKey) {
        return SpringContext.getBean(TemplateConfiguration.class).exists(templateKey);
    }

    public boolean exists(String templateName, Long version) {
        return SpringContext.getBean(TemplateConfiguration.class).exists(templateName, version);
    }

    public void insert(Template template) {
        if (exists(template.getName(), template.getVersion())) {
            throw new MetadataDoesNotExistException(String.format("Template with name %s and version %s already exists", template.getName(), template.getVersion()));
        }
        SpringContext.getBean(TemplateConfiguration.class).insert(template);
    }

    public Optional<Template> get(TemplateKey templateKey) {
        return SpringContext.getBean(TemplateConfiguration.class).get(templateKey);
    }

    public Optional<Template> get(String templateName, long version) {
        return SpringContext.getBean(TemplateConfiguration.class).getByNameAndVersion(templateName, version);
    }

    public void update(Template template) {
        if (!exists(template.getName(), template.getVersion())) {
            throw new MetadataDoesNotExistException(String.format("Template with name %s and version %s does not exist", template.getName(), template.getVersion()));
        }
        SpringContext.getBean(TemplateConfiguration.class).update(template);
    }

    public void delete(TemplateKey templateKey) {
        SpringContext.getBean(TemplateConfiguration.class).delete(templateKey);
    }

    public void delete(String name, long version) {
        SpringContext.getBean(TemplateConfiguration.class).deleteByNameAndVersion(name, version);
    }

    @Override
    public boolean matches(DataType dataType, Template template, ExecutionRuntime executionRuntime) {
        if (dataType instanceof InMemoryDatasetImplementation) {
            for (Matcher matcher : template.getMatchers()) {
                log.debug("validating " + matcher.toString() + " of template " + template.toString());
                if (!InMemoryDatasetImplementationService.getInstance().getDataItem((InMemoryDatasetImplementation) dataType, matcher.getKey(), executionRuntime)
                        .map(dataItem -> MatcherValueHandler.getInstance().matches(matcher.getMatcherValue(), dataItem, executionRuntime))
                        .orElse(false)) {
                    if (!InMemoryDatasetImplementationService.getInstance().getDataItem((InMemoryDatasetImplementation) dataType, matcher.getKey(), executionRuntime).isPresent()) {
                        log.warn("Dataset " + dataType.toString() + " does not contain item with key " + matcher.getKey());
                    } else {
                        log.warn(InMemoryDatasetImplementationService.getInstance().getDataItem((InMemoryDatasetImplementation) dataType, matcher.getKey(), executionRuntime).get().toString() + " does not match " + matcher.getMatcherValue().toString());
                    }
                    return false;
                }
            }
            return true;
        } else {
            throw new RuntimeException(MessageFormat.format("Cannot compare {0} to {1}", dataType, template));
        }
    }

}
