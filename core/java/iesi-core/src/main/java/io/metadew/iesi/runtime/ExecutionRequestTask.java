package io.metadew.iesi.runtime;

import io.metadew.iesi.framework.configuration.FrameworkConfiguration;
import io.metadew.iesi.framework.configuration.FrameworkFolderConfiguration;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.framework.execution.FrameworkRuntime;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

public class ExecutionRequestTask implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger();
    private ExecutionRequest executionRequest;

    public ExecutionRequestTask(ExecutionRequest executionRequest) {
        this.executionRequest = executionRequest;
    }

    @Override
    public void run() {
        ThreadContext.put("location", FrameworkFolderConfiguration.getInstance().getFolderAbsolutePath("logs"));
        ThreadContext.put("context.name", FrameworkExecution.getInstance().getFrameworkExecutionContext().getContext().getName());
        ThreadContext.put("context.scope", FrameworkExecution.getInstance().getFrameworkExecutionContext().getContext().getScope());
        ThreadContext.put("fwk.runid", FrameworkRuntime.getInstance().getFrameworkRunId());
        ThreadContext.put("fwk.code", FrameworkConfiguration.getInstance().getFrameworkCode());
        LOGGER.info("running " + executionRequest.getMetadataKey().getId());
        ExecutorService.getInstance().execute(executionRequest);
    }
}
