package io.metadew.iesi.metadata_repository;

import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.DataObject;
import io.metadew.iesi.metadata_repository.repository.Repository;

public class ControlMetadataRepository extends MetadataRepository {

    public ControlMetadataRepository(String frameworkCode, String name, String scope, String instanceName, Repository repository, String repositoryObjectsPath,  String repositoryTablesPath) {
        super(frameworkCode, name, scope, instanceName, repository, repositoryObjectsPath, repositoryTablesPath);
    }

    @Override
    public String getDefinitionFileName() {
        return "ControlTables.json";
    }

    @Override
    public String getObjectDefinitionFileName() {
        return "ControlObjects.json";
    }

    @Override
    public String getCategory() {
        return "control";
    }

    @Override
    public String getCategoryPrefix() {
        return "CTL";
    }

    @Override
    public void save(DataObject dataObject, FrameworkExecution frameworkExecution) {

    }
}
