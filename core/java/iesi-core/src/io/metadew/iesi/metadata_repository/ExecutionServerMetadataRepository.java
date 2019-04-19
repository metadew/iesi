package io.metadew.iesi.metadata_repository;

import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.DataObject;
import io.metadew.iesi.metadata_repository.repository.Repository;

import java.io.File;
import java.util.UUID;

public class ExecutionServerMetadataRepository extends MetadataRepository{

    public ExecutionServerMetadataRepository(String frameworkCode, String name, String scope, String instanceName, Repository repository, String repositoryObjectsPath,  String repositoryTablesPath) {
        super(frameworkCode, name, scope, instanceName, repository, repositoryObjectsPath, repositoryTablesPath);
    }

    @Override
    public String getDefinitionFileName() {
        return "ExecutionServerTables.json";
    }

    @Override
    public String getObjectDefinitionFileName() {
        return "ExecutionServerObjects.json";
    }

    @Override
    public String getCategory() {
        return "execution_server";
    }

    @Override
    public String getCategoryPrefix() {
        return "PRC";
    }

    @Override
    public void create(boolean generateDdl) {
        System.out.println("create");
    }

    @Override
    public void createMetadataRepository(File file, String archiveFolder, String errorFolder, UUID uuid) {
        System.out.println("create metadata repository");
    }

    @Override
    public void save(DataObject dataObject, FrameworkExecution frameworkExecution) {
        System.out.println("save");
    }
}
