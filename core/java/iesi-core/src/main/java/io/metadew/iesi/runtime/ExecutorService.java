package io.metadew.iesi.runtime;

import io.metadew.iesi.metadata.configuration.execution.ExecutionRequestConfiguration;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class ExecutorService {

    private final ExecutionRequestConfiguration executionRequestConfiguration;
    private Map<Class<? extends ExecutionRequest>, RequestExecutor> requestExecutorMap;

    private static final Logger LOGGER = LogManager.getLogger();
    private static ExecutorService INSTANCE;

    public synchronized static ExecutorService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ExecutorService();
        }
        return INSTANCE;
    }

    private ExecutorService() {
        requestExecutorMap = new HashMap<>();
        AuthenticatedRequestExecutor authenticatedRequestExecutor = AuthenticatedRequestExecutor.getInstance();
        NonAuthenticatedRequestExecutor nonAuthenticatedRequestExecutor = NonAuthenticatedRequestExecutor.getInstance();

        requestExecutorMap.put(authenticatedRequestExecutor.appliesTo(), authenticatedRequestExecutor);
        requestExecutorMap.put(nonAuthenticatedRequestExecutor.appliesTo(), nonAuthenticatedRequestExecutor);
        executionRequestConfiguration = new ExecutionRequestConfiguration();
    }

    @SuppressWarnings("unchecked")
    public void execute(ExecutionRequest executionRequest) {
        RequestExecutor requestExecutor = requestExecutorMap.get(executionRequest.getClass());
        if (requestExecutor == null) {
            LOGGER.error(MessageFormat.format("No Executor found for request type {0}", executionRequest.getClass()));
        } else {
            LOGGER.info(MessageFormat.format("Executing request {0}", executionRequest.getMetadataKey().getId()));
            requestExecutor.execute(executionRequest);
        }
    }

}
