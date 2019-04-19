package io.metadew.iesi.metadata_repository;

import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.DataObject;
import io.metadew.iesi.metadata_repository.repository.Repository;

import java.io.File;
import java.util.UUID;

public class TraceMetadataRepository extends MetadataRepository {

    public TraceMetadataRepository(String frameworkCode, String name, String scope, String instanceName, Repository repository, String repositoryObjectsPath, String repositoryTablesPath) {
        super(frameworkCode, name, scope, instanceName, repository, repositoryObjectsPath, repositoryTablesPath);
    }

    @Override
    public String getDefinitionFileName() {
        return "TraceTables.json";
    }

    @Override
    public String getObjectDefinitionFileName() {
        return "TraceObjects.json";
    }

    @Override
    public String getCategory() {
        return "trace";
    }


    @Override
    public String getCategoryPrefix() {
        return "TRC";
    }

    @Override
    public void create(boolean generateDdl) {

    }

    @Override
    public void createMetadataRepository(File file, String archiveFolder, String errorFolder, UUID uuid) {

    }

    @Override
    public void save(DataObject dataObject, FrameworkExecution frameworkExecution) {

    }
}
