package io.metadew.iesi.metadata.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.DataObject;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;

public class ResultMetadataRepository extends MetadataRepository {
    private static final Logger LOGGER = LogManager.getLogger();

    public ResultMetadataRepository(String frameworkCode, String name, String scope, String instanceName, RepositoryCoordinator repositoryCoordinator, String repositoryObjectsPath, String repositoryTablesPath) {
        super(frameworkCode, name, scope, instanceName, repositoryCoordinator, repositoryObjectsPath, repositoryTablesPath);
    }

    @Override
    public String getDefinitionFileName() {
        return "ResultTables.json";
    }

    @Override
    public String getObjectDefinitionFileName() {
        return "ResultObjects.json";
    }

    @Override
    public String getCategory() {
        return "result";
    }

    @Override
    public String getCategoryPrefix() {
        return "RES";
    }

    @Override
    public void save(DataObject dataObject, FrameworkExecution frameworkExecution) {
        ObjectMapper objectMapper = new ObjectMapper();
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
