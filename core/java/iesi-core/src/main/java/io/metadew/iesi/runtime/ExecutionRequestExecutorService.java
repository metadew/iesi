package io.metadew.iesi.runtime;

import io.metadew.iesi.metadata.definition.execution.ExecutionRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class ExecutionRequestExecutorService {

    private Map<Class<? extends ExecutionRequest>, ExecutionRequestExecutor> requestExecutorMap;

    private static final Logger LOGGER = LogManager.getLogger();
    private static ExecutionRequestExecutorService INSTANCE;

    public synchronized static ExecutionRequestExecutorService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ExecutionRequestExecutorService();
        }
        return INSTANCE;
    }

    private ExecutionRequestExecutorService() {
        requestExecutorMap = new HashMap<>();
        AuthenticatedExecutionRequestExecutor authenticatedRequestExecutor = AuthenticatedExecutionRequestExecutor.getInstance();
        NonAuthenticatedExecutionRequestExecutor nonAuthenticatedRequestExecutor = NonAuthenticatedExecutionRequestExecutor.getInstance();

        requestExecutorMap.put(authenticatedRequestExecutor.appliesTo(), authenticatedRequestExecutor);
        requestExecutorMap.put(nonAuthenticatedRequestExecutor.appliesTo(), nonAuthenticatedRequestExecutor);
    }

    @SuppressWarnings("unchecked")
    public void execute(ExecutionRequest executionRequest) {
        ExecutionRequestExecutor executionRequestExecutor = requestExecutorMap.get(executionRequest.getClass());
        if (executionRequestExecutor == null) {
            LOGGER.error(MessageFormat.format("No Executor found for request type {0}", executionRequest.getClass()));
        } else {
            LOGGER.info(MessageFormat.format("Executing request {0}", executionRequest.getMetadataKey().getId()));
            executionRequestExecutor.execute(executionRequest);
        }
    }

}
