package io.metadew.iesi.runtime;

import io.metadew.iesi.metadata.configuration.execution.ExecutionRequestConfiguration;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequestStatus;
import lombok.extern.log4j.Log4j2;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

@Log4j2
public class ExecutionRequestExecutorService {

    private Map<Class<? extends ExecutionRequest>, ExecutionRequestExecutor> requestExecutorMap;

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
        try {
            ExecutionRequestExecutor executionRequestExecutor = requestExecutorMap.get(executionRequest.getClass());
            if (executionRequestExecutor == null) {
                log.error(MessageFormat.format("No Executor found for request type {0}", executionRequest.getClass()));
                executionRequest.setExecutionRequestStatus(ExecutionRequestStatus.DECLINED);
                ExecutionRequestConfiguration.getInstance().update(executionRequest);
            } else {
                log.info(MessageFormat.format("Executing request {0}", executionRequest.getMetadataKey().getId()));
                executionRequestExecutor.execute(executionRequest);
                log.info(MessageFormat.format("Processed request {0}", executionRequest.getMetadataKey().getId()));
                executionRequest.setExecutionRequestStatus(ExecutionRequestStatus.COMPLETED);
                ExecutionRequestConfiguration.getInstance().update(executionRequest);
            }
        } catch (Exception e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            log.info("exception=" + e);
            log.debug("exception.stacktrace=" + stackTrace.toString());
        }
    }

}
