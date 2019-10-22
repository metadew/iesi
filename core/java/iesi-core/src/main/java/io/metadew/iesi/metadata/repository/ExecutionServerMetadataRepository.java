package io.metadew.iesi.metadata.repository;

import io.metadew.iesi.metadata.configuration.execution.ExecutionRequestConfiguration;
import io.metadew.iesi.metadata.configuration.execution.script.ScriptExecutionConfiguration;
import io.metadew.iesi.metadata.configuration.execution.script.ScriptExecutionRequestConfiguration;
import io.metadew.iesi.metadata.definition.DataObject;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;

public class ExecutionServerMetadataRepository extends MetadataRepository {

    public ExecutionServerMetadataRepository(String name, String scope, String instanceName, RepositoryCoordinator repositoryCoordinator) {
        super(name, scope, instanceName, repositoryCoordinator);
        ExecutionRequestConfiguration.getInstance().init(this);
        ScriptExecutionRequestConfiguration.getInstance().init(this);
        ScriptExecutionConfiguration.getInstance().init(this);
    }

    @Override
    public String getDefinitionFileName() {
        return "ExecutionTables.json";
    }

    @Override
    public String getObjectDefinitionFileName() {
        return "ExecutionObjects.json";
    }

    @Override
    public String getCategory() {
        return "execution_server";
    }

    @Override
    public String getCategoryPrefix() {
        return "EXE";
    }

    @Override
    public void save(DataObject dataObject) {}
}
