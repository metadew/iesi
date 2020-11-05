package io.metadew.iesi.metadata.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.metadata.configuration.connection.ConnectionConfiguration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.execution.ExecutionRequestConfiguration;
import io.metadew.iesi.metadata.configuration.execution.script.ScriptExecutionConfiguration;
import io.metadew.iesi.metadata.configuration.execution.script.ScriptExecutionRequestConfiguration;
import io.metadew.iesi.metadata.definition.DataObject;
import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.environment.Environment;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequest;
import io.metadew.iesi.metadata.definition.impersonation.Impersonation;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;

public class ExecutionServerMetadataRepository extends MetadataRepository {

    private static final Logger LOGGER = LogManager.getLogger();

    public ExecutionServerMetadataRepository(String instanceName, RepositoryCoordinator repositoryCoordinator) {
        super(instanceName, repositoryCoordinator);
    }

    @Override
    public String getCategory() {
        return "execution";
    }

    @Override
    public void save(DataObject dataObject) {
        // TODO: based on MetadataRepository object decide to insert or not insert the objects
        // TODO: insert should be handled on database level as insert can differ from database type/dialect? JDBC Dialect/Spring
        ObjectMapper objectMapper = new ObjectMapper();
        if (dataObject.getType().equalsIgnoreCase("executionRequest")) {
            ExecutionRequest executionRequest = (ExecutionRequest) objectMapper.convertValue(dataObject, Metadata.class);
            save(executionRequest);
//        else if (dataObject.getType().equalsIgnoreCase("environment")) {
//            Environment environment = (Environment) objectMapper.convertValue(dataObject, Metadata.class);
//            save(environment);
//        } else if (dataObject.getType().equalsIgnoreCase("impersonation")) {
//            Impersonation impersonation = (Impersonation) objectMapper.convertValue(dataObject, Metadata.class);
//            save(impersonation);
        } else if (dataObject.getType().equalsIgnoreCase("repository")) {
            // TODO
        } else {
            LOGGER.trace(MessageFormat.format("Execution repository is not responsible for loading saving {0}", dataObject.getType()));
        }
    }
    public void save(ExecutionRequest executionRequest) {
        LOGGER.info(MessageFormat.format("Inserting connection {0}-{1} into execution repository",
                executionRequest.getMetadataKey().getId()));
        try {
            ExecutionRequestConfiguration.getInstance().insert(executionRequest);
        } catch (MetadataAlreadyExistsException e1) {
            LOGGER.info(MessageFormat.format("Execution {0}-{1} already exists in execution repository. Updating execution {0}-{1} instead.",
                    executionRequest.getMetadataKey().getId()));
            ExecutionRequestConfiguration.getInstance().update(executionRequest);
        }
    }
}
