package io.metadew.iesi.metadata.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.framework.execution.FrameworkExecution;
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

import java.text.MessageFormat;

public class DesignMetadataRepository extends MetadataRepository {

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
    public void save(DataObject dataObject, FrameworkExecution frameworkExecution) throws MetadataRepositorySaveException {
        // TODO: based on MetadataRepository object decide to insert or not insert the objects
        // TODO: insert should be handled on database level as insert can differ from database type/dialect? JDBC Dialect/Spring
        ObjectMapper objectMapper = new ObjectMapper();
        if (dataObject.getType().equalsIgnoreCase("script")) {
            Script script = objectMapper.convertValue(dataObject.getData(), Script.class);
            save(script, frameworkExecution);
        } else if (dataObject.getType().equalsIgnoreCase("component")) {
            Component component = objectMapper.convertValue(dataObject.getData(), Component.class);
            save(component, frameworkExecution);
        } else if (dataObject.getType().equalsIgnoreCase("generation")) {
            Generation generation= objectMapper.convertValue(dataObject.getData(), Generation.class);
            save(generation, frameworkExecution);
        } else if (dataObject.getType().equalsIgnoreCase("subroutine")) {
            System.out.println("subroutine");
            // TODO
        } else {
            frameworkExecution.getFrameworkLog().log(MessageFormat.format("Design repository is not responsible for loading saving {0}", dataObject.getType()), Level.TRACE);
        }
    }

    public void save(Script script, FrameworkExecution frameworkExecution) throws MetadataRepositorySaveException {
        frameworkExecution.getFrameworkLog().log(MessageFormat.format("Saving script {0}-{1} into design repository", script.getName(), script.getVersion().getNumber()), Level.INFO);
        ScriptConfiguration scriptConfiguration = new ScriptConfiguration(frameworkExecution.getFrameworkInstance());
        try {
            scriptConfiguration.insertScript(script);
        } catch (ScriptAlreadyExistsException e) {
            frameworkExecution.getFrameworkLog().log(MessageFormat.format("Script {0}-{1} already exists in design repository. Updating to new definition", script.getName(), script.getVersion().getNumber()), Level.INFO);
            try {
                scriptConfiguration.updateScript(script);
            } catch (ScriptDoesNotExistException ex) {
                throw new MetadataRepositorySaveException(ex);

            }
        }
    }

    public void save(Component component, FrameworkExecution frameworkExecution) throws MetadataRepositorySaveException {
        frameworkExecution.getFrameworkLog().log(MessageFormat.format("Saving component {0} into design repository", component.getName()), Level.INFO);
        ComponentConfiguration componentConfiguration = new ComponentConfiguration(frameworkExecution.getFrameworkInstance());
        try {
            componentConfiguration.insertComponent(component);
        } catch (ComponentAlreadyExistsException e) {
            frameworkExecution.getFrameworkLog().log(MessageFormat.format("Component {0} already exists in design repository. Updating to new definition", component.getName()), Level.INFO);
            try {
                componentConfiguration.updateComponent(component);
            } catch (ComponentDoesNotExistException ex) {
                throw new MetadataRepositorySaveException(ex);
            }
        }
    }
    
    public void save(Generation generation, FrameworkExecution frameworkExecution) {
        frameworkExecution.getFrameworkLog().log(MessageFormat.format("Saving generation {0} into design repository", generation.getName()), Level.INFO);
        GenerationConfiguration generationConfiguration = new GenerationConfiguration(generation,
                frameworkExecution.getFrameworkInstance());
        executeUpdate(generationConfiguration.getInsertStatement());
    }

}
