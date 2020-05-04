package io.metadew.iesi.metadata.repository;

import io.metadew.iesi.metadata.configuration.action.performance.ActionPerformanceConfiguration;
import io.metadew.iesi.metadata.configuration.action.result.ActionResultConfiguration;
import io.metadew.iesi.metadata.configuration.action.result.ActionResultOutputConfiguration;
import io.metadew.iesi.metadata.configuration.script.result.ScriptResultConfiguration;
import io.metadew.iesi.metadata.configuration.script.result.ScriptResultOutputConfiguration;
import io.metadew.iesi.metadata.definition.DataObject;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;

public class ResultMetadataRepository extends MetadataRepository {
    private static final Logger LOGGER = LogManager.getLogger();

    public ResultMetadataRepository(String instanceName, RepositoryCoordinator repositoryCoordinator) {
        super(instanceName, repositoryCoordinator);
        ScriptResultConfiguration.getInstance().init(this);
        ActionResultConfiguration.getInstance().init(this);
        ActionResultOutputConfiguration.getInstance().init(this);
        ScriptResultOutputConfiguration.getInstance().init(this);
        ActionPerformanceConfiguration.getInstance().init(this);
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
