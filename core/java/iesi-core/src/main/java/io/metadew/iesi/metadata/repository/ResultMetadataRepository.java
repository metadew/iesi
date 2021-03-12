package io.metadew.iesi.metadata.repository;

import io.metadew.iesi.metadata.definition.DataObject;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;

public class ResultMetadataRepository extends MetadataRepository {
    private static final Logger LOGGER = LogManager.getLogger();

    public ResultMetadataRepository(String instanceName, RepositoryCoordinator repositoryCoordinator) {
        super(instanceName, repositoryCoordinator);
    }

    @Override
    public String getCategory() {
        return "result";
    }

    @Override
    public void save(DataObject dataObject) {
        if (dataObject.getType().equalsIgnoreCase("log")) {
//            Script script = objectMapper.convertValue(dataObject.getData(), Script.class);
//            ScriptConfiguration scriptConfiguration = new ScriptConfiguration(script,
//                    frameworkExecution.getFrameworkInstance());
//            executeUpdate(scriptConfiguration.getInsertStatement());
        } else {
            LOGGER.trace(MessageFormat.format("Result repository is not responsible for loading saving {0}", dataObject.getType()));
        }
    }
}
