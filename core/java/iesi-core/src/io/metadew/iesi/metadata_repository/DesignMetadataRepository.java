package io.metadew.iesi.metadata_repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.configuration.*;
import io.metadew.iesi.metadata.definition.*;
import io.metadew.iesi.metadata_repository.repository.Repository;
import io.metadew.iesi.script.operation.ScriptOperation;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.text.MessageFormat;
import java.util.UUID;

public class DesignMetadataRepository extends MetadataRepository {

    public DesignMetadataRepository(String frameworkCode, String name, String scope, String instanceName, Repository repository,  String repositoryObjectsPath, String repositoryTablesPath ) {
        super(frameworkCode, name, scope, instanceName, repository, repositoryObjectsPath, repositoryTablesPath);
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
    public void create(boolean generateDdl) {
    }

    @Override
    public void createMetadataRepository(File file, String archiveFolder, String errorFolder, UUID uuid) {

    }

    @Override
    public void save(DataObject dataObject, FrameworkExecution frameworkExecution) {
        // TODO: based on MetadataRepository object decide to insert or not insert the objects
        // TODO: insert should be handled on database level as insert can differ from database type/dialect? JDBC Dialect/Spring
        ObjectMapper objectMapper = new ObjectMapper();
        if (dataObject.getType().equalsIgnoreCase("script")) {
            Script script = objectMapper.convertValue(dataObject.getData(), Script.class);
            ScriptConfiguration scriptConfiguration = new ScriptConfiguration(script,
                    frameworkExecution);
            executeUpdate(scriptConfiguration.getInsertStatement());
        } else if (dataObject.getType().equalsIgnoreCase("component")) {
            Component component = objectMapper.convertValue(dataObject.getData(), Component.class);
            ComponentConfiguration componentConfiguration = new ComponentConfiguration(component,
                    frameworkExecution);
            executeUpdate(componentConfiguration.getInsertStatement());
        } else if (dataObject.getType().equalsIgnoreCase("subroutine")) {
            // TODO
        } else 	{
            frameworkExecution.getFrameworkLog().log(MessageFormat.format("This repository is not responsible for loading saving {0}", dataObject.getType()), Level.WARN);
        }
    }


}
