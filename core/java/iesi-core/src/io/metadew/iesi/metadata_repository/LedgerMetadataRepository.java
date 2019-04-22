package io.metadew.iesi.metadata_repository;

import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.DataObject;
import io.metadew.iesi.metadata_repository.repository.Repository;

public class LedgerMetadataRepository extends MetadataRepository {

    public LedgerMetadataRepository(String frameworkCode, String name, String scope, String instanceName, Repository repository, String repositoryObjectsPath, String repositoryTablesPath) {
        super(frameworkCode, name, scope, instanceName, repository, repositoryObjectsPath, repositoryTablesPath);
    }

    @Override
    public String getDefinitionFileName() {
        return null;
    }

    @Override
    public String getObjectDefinitionFileName() {
        return null;
    }

    @Override
    public String getCategory() {
        return null;
    }

    @Override
    public String getCategoryPrefix() {
        return null;
    }

    @Override
    public void save(DataObject dataObject, FrameworkExecution frameworkExecution) {

    }
}
