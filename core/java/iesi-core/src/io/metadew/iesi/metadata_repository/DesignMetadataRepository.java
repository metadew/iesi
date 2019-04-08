package io.metadew.iesi.metadata_repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.configuration.*;
import io.metadew.iesi.metadata.definition.*;
import io.metadew.iesi.metadata_repository.repository.Repository;
import io.metadew.iesi.script.operation.ScriptOperation;
import org.apache.logging.log4j.Level;

import java.io.File;
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
    public void createAllTables() {

    }

    @Override
    public void createMetadataRepository(File file, String archiveFolder, String errorFolder, UUID uuid) {

    }

    @Override
    public void save(DataObject dataObject, FrameworkExecution frameworkExecution) {
    }


}
