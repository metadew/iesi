package io.metadew.iesi.metadata.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.configuration.ComponentConfiguration;
import io.metadew.iesi.metadata.configuration.GenerationConfiguration;
import io.metadew.iesi.metadata.configuration.exception.ComponentAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.ComponentDoesNotExistException;
import io.metadew.iesi.metadata.configuration.exception.ScriptAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.ScriptDoesNotExistException;
import io.metadew.iesi.metadata.configuration.script.ScriptConfiguration;
import io.metadew.iesi.metadata.definition.Component;
import io.metadew.iesi.metadata.definition.DataObject;
import io.metadew.iesi.metadata.definition.Generation;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;

public class DesignMetadataRepository extends MetadataRepository {
    private static final Logger LOGGER = LogManager.getLogger();

    public DesignMetadataRepository(String frameworkCode, String name, String scope, String instanceName, RepositoryCoordinator repositoryCoordinator, String repositoryObjectsPath, String repositoryTablesPath) {
        super(frameworkCode, name, scope, instanceName, repositoryCoordinator, repositoryObjectsPath, repositoryTablesPath);
    }

    @Override
    public String getDefinitionFileName() {
        return "DesignTables.json";
    }

    @Override
    public String getObjectDefinitionFileName() {
        return "DesignObjects.json";
    }

    @Override
    public String getCategory() {
        return "design";
    }


    @Override
    public String getCategoryPrefix() {
        return "DES";
    }

    @Override
    public void save(DataObject dataObject) throws MetadataRepositorySaveException {
        // TODO: based on MetadataRepository object decide to insert or not insert the objects
        // TODO: insert should be handled on database level as insert can differ from database type/dialect? JDBC Dialect/Spring
        ObjectMapper objectMapper = new ObjectMapper();
        if (dataObject.getType().equalsIgnoreCase("script")) {
            Script script = objectMapper.convertValue(dataObject.getData(), Script.class);
            save(script);
        } else if (dataObject.getType().equalsIgnoreCase("component")) {
            Component component = objectMapper.convertValue(dataObject.getData(), Component.class);
            save(component);
        } else if (dataObject.getType().equalsIgnoreCase("generation")) {
            Generation generation= objectMapper.convertValue(dataObject.getData(), Generation.class);
            save(generation);
        } else if (dataObject.getType().equalsIgnoreCase("subroutine")) {
            System.out.println("subroutine");
            // TODO
        } else {
            LOGGER.trace(MessageFormat.format("Design repository is not responsible for loading saving {0}", dataObject.getType()));
        }
    }

    public void save(Script script) throws MetadataRepositorySaveException {
        LOGGER.info(MessageFormat.format("Saving script {0}-{1} into design repository", script.getName(), script.getVersion().getNumber()));
        ScriptConfiguration scriptConfiguration = new ScriptConfiguration();
        try {
            scriptConfiguration.insert(script);
        } catch (ScriptAlreadyExistsException e) {
            LOGGER.info(MessageFormat.format("Script {0}-{1} already exists in design repository. Updating to new definition", script.getName(), script.getVersion().getNumber()));
            try {
                scriptConfiguration.update(script);
            } catch (ScriptDoesNotExistException ex) {
                throw new MetadataRepositorySaveException(ex);

            }
        }
    }

    public void save(Component component) throws MetadataRepositorySaveException {
        LOGGER.info(MessageFormat.format("Saving component {0} into design repository", component.getName()));
        ComponentConfiguration componentConfiguration = new ComponentConfiguration();
        try {
            componentConfiguration.insertComponent(component);
        } catch (ComponentAlreadyExistsException e) {
            LOGGER.warn(MessageFormat.format("Component {0} already exists in design repository. Updating to new definition", component.getName()), Level.INFO);
            try {
                componentConfiguration.updateComponent(component);
            } catch (ComponentDoesNotExistException ex) {
                throw new MetadataRepositorySaveException(ex);
            }
        }
    }
    
    public void save(Generation generation) {
        LOGGER.info(MessageFormat.format("Saving generation {0} into design repository", generation.getName()));
        GenerationConfiguration generationConfiguration = new GenerationConfiguration(generation);
        executeUpdate(generationConfiguration.getInsertStatement());
    }

}
