package io.metadew.iesi.metadata.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.SpringContext;
import io.metadew.iesi.metadata.configuration.component.ComponentConfiguration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.script.ScriptConfiguration;
import io.metadew.iesi.metadata.configuration.template.TemplateConfiguration;
import io.metadew.iesi.metadata.definition.DataObject;
import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.action.Action;
import io.metadew.iesi.metadata.definition.component.Component;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.metadata.definition.security.SecurityGroup;
import io.metadew.iesi.metadata.definition.subroutine.Subroutine;
import io.metadew.iesi.metadata.definition.template.Template;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;
import io.metadew.iesi.metadata.service.security.SecurityGroupService;
import lombok.extern.log4j.Log4j2;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
public class DesignMetadataRepository extends MetadataRepository {

    private final ScriptConfiguration scriptConfiguration = SpringContext.getBean(ScriptConfiguration.class);
    private final TemplateConfiguration templateConfiguration = SpringContext.getBean(TemplateConfiguration.class);
    private final ComponentConfiguration componentConfiguration = SpringContext.getBean(ComponentConfiguration.class);
    private final SecurityGroupService securityGroupService = SpringContext.getBean(SecurityGroupService.class);

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
            Subroutine subroutine = objectMapper.convertValue(dataObject, Subroutine.class);
            log.warn(String.format("Trying to save %s. Not implemented yet", subroutine));
        } else if (dataObject.getType().equalsIgnoreCase("template")) {
            Template template = objectMapper.convertValue(dataObject.getData(), Template.class);
            save(template);
        } else {
            log.trace(MessageFormat.format("Design repository is not responsible for loading saving {0}", dataObject.getType()));
        }
    }

    public void save(Script script) {
        log.info(MessageFormat.format("Saving {0} into design repository", script));
        if (!verifyScript(script)) {
            log.error(MessageFormat.format("Script {0} cannot be saved as it contains errors", script));
            return;
        }
        try {
            // if a script does not have a security group, it is linked to the PUBLIC security group
            if (script.getSecurityGroupKey() == null) {
                log.warn("{0} not linked to a security group, linking it to the public security group");
                SecurityGroup publicSecurityGroup = securityGroupService.get("PUBLIC")
                        .orElseThrow(() -> new RuntimeException("Could not find security group with name PUBLIC"));
                script.setSecurityGroupKey(publicSecurityGroup.getMetadataKey());
                script.setSecurityGroupName(publicSecurityGroup.getName());
            }

            script.getVersion().setCreatedBy("admin");
            script.getVersion().setCreatedAt(LocalDateTime.now().toString());
            scriptConfiguration.insert(script);
        } catch (MetadataAlreadyExistsException e) {
            log.info(MessageFormat.format("Script {0}-{1} already exists in design repository. Updating to new definition", script.getName(), script.getVersion().getNumber()));
            script.getVersion().setLastModifiedBy("admin");
            script.getVersion().setLastModifiedAt(LocalDateTime.now().toString());
            scriptConfiguration.update(script);
        }
    }

    public void save(Template template) {
        log.info(MessageFormat.format("Saving {0} into design repository", template));
        try {
            templateConfiguration.insert(template);
        } catch (Exception e) {
            log.info(MessageFormat.format("Template {0} already exists in design repository. Updating to new definition", template));
            templateConfiguration.update(template);
        }

    }

    public void save(Component component) {
        log.info(MessageFormat.format("Saving {0} into design repository", component));
        try {
            if (component.getSecurityGroupKey() == null) {
                log.warn("{0} not linked to a security group, linking it to the public security group");
                SecurityGroup publicSecurityGroup = securityGroupService.get("PUBLIC")
                        .orElseThrow(() -> new RuntimeException("Could not find security group with name PUBLIC"));
                component.setSecurityGroupKey(publicSecurityGroup.getMetadataKey());
                component.setSecurityGroupName(publicSecurityGroup.getName());
            }

            componentConfiguration.insert(component);
        } catch (MetadataAlreadyExistsException e) {
            log.warn(MessageFormat.format("{0} already exists in design repository. Updating to new definition", component));
            componentConfiguration.update(component);
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
            log.error(MessageFormat.format("Script {0}-{1} has duplicate parameters: {2}", script.getName(), script.getVersion().getNumber(), duplicateParameters));
        }
        List<String> actionNames = script.getActions().stream().map(Action::getName).collect(Collectors.toList());
        List<String> duplicateActions = actionNames.stream().filter(i -> Collections.frequency(actionNames, i) > 1).collect(Collectors.toList());
        if (duplicateActions.size() > 1) {
            log.error(MessageFormat.format("Script {0}-{1} has duplicate actions: {2}", script.getName(), script.getVersion().getNumber(), duplicateActions));
        }
        return duplicateParameters.isEmpty() && duplicateActions.isEmpty();

    }

}
