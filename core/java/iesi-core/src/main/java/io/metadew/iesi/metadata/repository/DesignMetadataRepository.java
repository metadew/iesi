package io.metadew.iesi.metadata.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.configuration.ComponentConfiguration;
import io.metadew.iesi.metadata.configuration.GenerationConfiguration;
import io.metadew.iesi.metadata.configuration.ScriptConfiguration;
import io.metadew.iesi.metadata.definition.Component;
import io.metadew.iesi.metadata.definition.DataObject;
import io.metadew.iesi.metadata.definition.Generation;
import io.metadew.iesi.metadata.definition.Script;
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
    public void save(DataObject dataObject, FrameworkExecution frameworkExecution) {
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
            // TODO
        } else {
            frameworkExecution.getFrameworkLog().log(MessageFormat.format("Design repository is not responsible for loading saving {0}", dataObject.getType()), Level.TRACE);
        }
    }

    public void save(Script script, FrameworkExecution frameworkExecution) {
        ScriptConfiguration scriptConfiguration = new ScriptConfiguration(script,
                frameworkExecution.getFrameworkInstance());
        executeUpdate(scriptConfiguration.getInsertStatement());
    }

    public void save(Component component, FrameworkExecution frameworkExecution) {
        ComponentConfiguration componentConfiguration = new ComponentConfiguration(component,
                frameworkExecution.getFrameworkInstance());
        executeUpdate(componentConfiguration.getInsertStatement());
    }
    
    public void save(Generation generation, FrameworkExecution frameworkExecution) {
        GenerationConfiguration generationConfiguration = new GenerationConfiguration(generation,
                frameworkExecution.getFrameworkInstance());
        executeUpdate(generationConfiguration.getInsertStatement());
    }

}
