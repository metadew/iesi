package io.metadew.iesi.runtime;

import io.metadew.iesi.metadata.configuration.execution.ExecutionRequestConfiguration;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequestStatus;
import io.metadew.iesi.metadata.definition.execution.NonAuthenticatedExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequest;
import io.metadew.iesi.runtime.script.ScriptExecutorService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NonAuthenticatedExecutionRequestExecutor implements ExecutionRequestExecutor<NonAuthenticatedExecutionRequest> {

    private static final Logger LOGGER = LogManager.getLogger();

    private static NonAuthenticatedExecutionRequestExecutor INSTANCE;

    public synchronized static NonAuthenticatedExecutionRequestExecutor getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NonAuthenticatedExecutionRequestExecutor();
        }
        return INSTANCE;
    }

    private NonAuthenticatedExecutionRequestExecutor() {
    }

    @Override
    public Class<NonAuthenticatedExecutionRequest> appliesTo() {
        return NonAuthenticatedExecutionRequest.class;
    }

    @Override
    public void execute(NonAuthenticatedExecutionRequest executionRequest) {
        executionRequest.updateExecutionRequestStatus(ExecutionRequestStatus.ACCEPTED);
        ExecutionRequestConfiguration.getInstance().update(executionRequest);

        for (ScriptExecutionRequest scriptExecutionRequest : executionRequest.getScriptExecutionRequests()) {
            ScriptExecutorService.getInstance().execute(scriptExecutionRequest);
        }
        executionRequest.updateExecutionRequestStatus(ExecutionRequestStatus.COMPLETED);
        ExecutionRequestConfiguration.getInstance().update(executionRequest);
    }
}
