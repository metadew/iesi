package io.metadew.iesi.runtime.script;

import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.configuration.execution.script.ScriptExecutionRequestConfiguration;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequestStatus;
import io.metadew.iesi.runtime.AuthenticatedRequestExecutor;
import io.metadew.iesi.script.ScriptExecutionBuildException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class ScriptExecutorService {

    private Map<Class<? extends ScriptExecutionRequest>, ScriptExecutor> scriptExecutorMap;
    private final ScriptExecutionRequestConfiguration scriptExecutionRequestConfiguration;

    private static final Logger LOGGER = LogManager.getLogger();
    private static ScriptExecutorService INSTANCE;

    public synchronized static ScriptExecutorService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ScriptExecutorService();
        }
        return INSTANCE;
    }

    private ScriptExecutorService() {
        scriptExecutorMap = new HashMap<>();
        ScriptFileExecutor scriptFileExecutor = ScriptFileExecutor.getInstance();
        ScriptNameExecutor scriptNameExecutor = ScriptNameExecutor.getInstance();

        scriptExecutorMap.put(scriptFileExecutor.appliesTo(), scriptFileExecutor);
        scriptExecutorMap.put(scriptNameExecutor.appliesTo(), scriptNameExecutor);
        scriptExecutionRequestConfiguration = new ScriptExecutionRequestConfiguration();
    }

    @SuppressWarnings("unchecked")
    public void execute(ScriptExecutionRequest scriptExecutionRequest) throws SQLException, MetadataDoesNotExistException, MetadataAlreadyExistsException, ScriptExecutionBuildException {
        ScriptExecutor scriptExecutor = scriptExecutorMap.get(scriptExecutionRequest.getClass());

        scriptExecutionRequest.updateScriptExecutionRequestStatus(ScriptExecutionRequestStatus.SUBMITTED);
        scriptExecutionRequestConfiguration.update(scriptExecutionRequest);

        if (scriptExecutor == null) {
            LOGGER.error(MessageFormat.format("No Executor found for request type {0}", scriptExecutionRequest.getClass()));
            scriptExecutionRequest.updateScriptExecutionRequestStatus(ScriptExecutionRequestStatus.DECLINED);
            scriptExecutionRequestConfiguration.update(scriptExecutionRequest);
        } else {
            scriptExecutionRequest.updateScriptExecutionRequestStatus(ScriptExecutionRequestStatus.ACCEPTED);
            scriptExecutionRequestConfiguration.update(scriptExecutionRequest);

            scriptExecutor.execute(scriptExecutionRequest);

            scriptExecutionRequest.updateScriptExecutionRequestStatus(ScriptExecutionRequestStatus.COMPLETED);
            scriptExecutionRequestConfiguration.update(scriptExecutionRequest);
        }
    }
}
