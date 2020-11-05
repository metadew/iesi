package io.metadew.iesi.metadata.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.metadata.configuration.component.ComponentConfiguration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.script.ScriptConfiguration;
import io.metadew.iesi.metadata.configuration.template.TemplateConfiguration;
import io.metadew.iesi.metadata.definition.DataObject;
import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.action.Action;
import io.metadew.iesi.metadata.definition.component.Component;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.metadata.definition.template.Template;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class DesignMetadataRepository extends MetadataRepository {
    private static final Logger LOGGER = LogManager.getLogger();

    public DesignMetadataRepository(String instance, RepositoryCoordinator repositoryCoordinator) {
        super(instance, repositoryCoordinator);
    }

    @Override
    public String getCategory() {
        return "design";
    }

    @Override
    public void save(DataObject dataObject) throws MetadataRepositorySaveException {
        // TODO: based on MetadataRepository object decide to insert or not insert the objects
        // TODO: insert should be handled on database level as insert can differ from database type/dialect? JDBC Dialect/Spring
        ObjectMapper objectMapper = new ObjectMapper();
        if (dataObject.getType().equalsIgnoreCase("script")) {
            Script script = (Script) objectMapper.convertValue(dataObject, Metadata.class);
            save(script);
        } else if (dataObject.getType().equalsIgnoreCase("component")) {
            Component component = (Component) objectMapper.convertValue(dataObject, Metadata.class);
            save(component);
        } else if (dataObject.getType().equalsIgnoreCase("subroutine")) {
            System.out.println("subroutine");
        } else if (dataObject.getType().equalsIgnoreCase("template")) {
            Template template = objectMapper.convertValue(dataObject.getData(), Template.class);
            save(template);
        } else {
            LOGGER.trace(MessageFormat.format("Design repository is not responsible for loading saving {0}", dataObject.getType()));
        }
    }

    public void save(Script script) {
        LOGGER.info(MessageFormat.format("Saving script {0}-{1} into design repository", script.getName(), script.getVersion().getNumber()));
        if (!verifyScript(script)) {
            LOGGER.error(MessageFormat.format("Script {0}-{1} cannot be saved as it contains errors", script.getName(), script.getVersion().getNumber()));
            return;
        }
        try {
            ScriptConfiguration.getInstance().insert(script);
        } catch (MetadataAlreadyExistsException e) {
            LOGGER.info(MessageFormat.format("Script {0}-{1} already exists in design repository. Updating to new definition", script.getName(), script.getVersion().getNumber()));
            ScriptConfiguration.getInstance().update(script);
        }
    }

    public void save(Template template) {
        LOGGER.info(MessageFormat.format("Saving {0} into design repository", template));
        try {
            TemplateConfiguration.getInstance().insert(template);
        } catch (MetadataAlreadyExistsException e) {
            LOGGER.info(MessageFormat.format("Template {0} already exists in design repository. Updating to new definition", template));
            TemplateConfiguration.getInstance().update(template);
        }

    }

    public void save(Component component) {
        LOGGER.info(MessageFormat.format("Saving component {0} into design repository", component.getName()));
        try {
            ComponentConfiguration.getInstance().insert(component);
        } catch (MetadataAlreadyExistsException e) {
            LOGGER.warn(MessageFormat.format("Component {0} already exists in design repository. Updating to new definition", component.getName()), Level.INFO);
            ComponentConfiguration.getInstance().update(component);
        }
    }

    private boolean verifyScript(Script script) {
        List<String> parameterNames = script.getParameters().stream()
                .map(parameter -> parameter.getMetadataKey().getParameterName())
                .collect(Collectors.toList());
        List<String> duplicateParameters = parameterNames.stream()
                .filter(parameter -> Collections.frequency(parameterNames, parameter) > 1)
                .collect(Collectors.toList());
        if (duplicateParameters.size() > 1) {
            LOGGER.error(MessageFormat.format("Script {0}-{1} has duplicate parameters: {2}", script.getName(), script.getVersion().getNumber(), duplicateParameters.toString()));
        }
        List<String> actionNames = script.getActions().stream().map(Action::getName).collect(Collectors.toList());
        List<String> duplicateActions = actionNames.stream().filter(i -> Collections.frequency(actionNames, i) > 1).collect(Collectors.toList());
        if (duplicateActions.size() > 1) {
            LOGGER.error(MessageFormat.format("Script {0}-{1} has duplicate actions: {2}", script.getName(), script.getVersion().getNumber(), duplicateActions.toString()));
        }
        return duplicateParameters.size() == 0 && duplicateActions.size() == 0;

    }

}
