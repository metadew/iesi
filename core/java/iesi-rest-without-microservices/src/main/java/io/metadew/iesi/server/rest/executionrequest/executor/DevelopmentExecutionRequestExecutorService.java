package io.metadew.iesi.server.rest.executionrequest.executor;

import io.metadew.iesi.metadata.configuration.execution.ExecutionRequestConfiguration;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequestStatus;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Log4j2
@Service
@ConditionalOnWebApplication
@Profile("single_process")
public class DevelopmentExecutionRequestExecutorService implements IExecutionRequestExecutorService {

    private final Map<Class<? extends ScriptExecutionRequest>, IExecutionRequestExecutor> executionRequestExecutorMap = new HashMap<>();
    private final ExecutionRequestConfiguration executionRequestConfiguration;
    private final ThreadPoolTaskExecutor executor;
    private final List<IExecutionRequestExecutor> executionRequestExecutors;

    public DevelopmentExecutionRequestExecutorService(ExecutionRequestConfiguration executionRequestConfiguration,
                                                      ThreadPoolTaskExecutor executor, List<IExecutionRequestExecutor> executionRequestExecutors) {
        this.executionRequestConfiguration = executionRequestConfiguration;
        this.executor = executor;
        this.executionRequestExecutors = executionRequestExecutors;
    }

    @PostConstruct
    @SuppressWarnings("unchecked")
    public void init() {
        executionRequestExecutors
                .forEach(scriptExecutor -> executionRequestExecutorMap.put(scriptExecutor.appliesTo(), scriptExecutor));
    }

    @SuppressWarnings("unchecked")
    @Async("executionRequestTaskExecutor")
    public CompletableFuture<Boolean> execute(ExecutionRequest executionRequest) {
        executionRequest.setExecutionRequestStatus(ExecutionRequestStatus.SUBMITTED);
        executionRequestConfiguration.update(executionRequest);
        try {
            IExecutionRequestExecutor executionRequestExecutor = executionRequestExecutorMap.get(executionRequest.getClass());
            if (executionRequestExecutor == null) {
                log.error(MessageFormat.format("No Executor found for request type {0}", executionRequest.getClass()));
                executionRequest.setExecutionRequestStatus(ExecutionRequestStatus.DECLINED);
                executionRequestConfiguration.update(executionRequest);
            } else {
                log.info(MessageFormat.format("Executing request {0}", executionRequest.getMetadataKey().getId()));
                executionRequestExecutor.execute(executionRequest);
                executionRequest.setExecutionRequestStatus(ExecutionRequestStatus.COMPLETED);
                executionRequestConfiguration.update(executionRequest);
                log.info(MessageFormat.format("Processed request {0}", executionRequest.getMetadataKey().getId()));
            }
        } catch (Exception e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            log.info("exception=" + e);
            log.debug("exception.stacktrace=" + stackTrace.toString());
            executionRequest.setExecutionRequestStatus(ExecutionRequestStatus.STOPPED);
            executionRequestConfiguration.update(executionRequest);
            return CompletableFuture.completedFuture(false);
        }
        return CompletableFuture.completedFuture(true);
    }

}
