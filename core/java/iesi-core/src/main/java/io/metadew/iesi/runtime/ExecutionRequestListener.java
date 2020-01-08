package io.metadew.iesi.runtime;

import io.metadew.iesi.framework.configuration.FrameworkConfiguration;
import io.metadew.iesi.framework.configuration.FrameworkFolderConfiguration;
import io.metadew.iesi.framework.configuration.FrameworkSettingConfiguration;
import io.metadew.iesi.framework.execution.FrameworkControl;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.framework.execution.FrameworkRuntime;
import io.metadew.iesi.metadata.configuration.execution.ExecutionRequestConfiguration;
import io.metadew.iesi.metadata.configuration.execution.exception.ExecutionRequestDoesNotExistException;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequestStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.*;

public class ExecutionRequestListener implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger();
    private final ExecutorService executor;
    private final Thread executionRequestMonitorThread;
    private boolean keepRunning = true;

    public ExecutionRequestListener() {
        int threadSize = FrameworkSettingConfiguration.getInstance().getSettingPath("server.threads")
                .map(settingPath -> Integer.parseInt(FrameworkControl.getInstance().getProperty(settingPath)))
                .orElse(4);
        LOGGER.info(MessageFormat.format("starting listener with thread pool size {0}", threadSize));
        executor = Executors.newFixedThreadPool(threadSize);
        executionRequestMonitorThread = new Thread(ExecutionRequestMonitor.getInstance());
        executionRequestMonitorThread.start();
    }

    public void run() {
        ThreadContext.put("location", FrameworkFolderConfiguration.getInstance().getFolderAbsolutePath("logs"));
        ThreadContext.put("context.name", FrameworkExecution.getInstance().getFrameworkExecutionContext().getContext().getName());
        ThreadContext.put("context.scope", FrameworkExecution.getInstance().getFrameworkExecutionContext().getContext().getScope());
        ThreadContext.put("fwk.runid", FrameworkRuntime.getInstance().getFrameworkRunId());
        ThreadContext.put("fwk.code", FrameworkConfiguration.getInstance().getFrameworkCode());
        try {
            while (keepRunning) {
                LOGGER.trace("executionrequestlistener=fetching new requests");
                List<ExecutionRequest> executionRequests = ExecutionRequestConfiguration.getInstance().getAllNew();
                LOGGER.trace(MessageFormat.format("executionrequestlistener=found {0} Requests", executionRequests.size()));
                for (ExecutionRequest executionRequest : executionRequests) {
                    LOGGER.info(MessageFormat.format("executionrequestlistener=submitting request {0} for execution", executionRequest.getMetadataKey().getId()));
                    executionRequest.updateExecutionRequestStatus(ExecutionRequestStatus.SUBMITTED);
                    ExecutionRequestConfiguration.getInstance().update(executionRequest);
                    executor.submit(new ExecutionRequestTask(executionRequest));
                }
                Thread.sleep(1000);
            }
        } catch (ExecutionRequestDoesNotExistException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void shutdown() throws InterruptedException {
        keepRunning = false;
        LOGGER.info("shutting down execution listener...");
        if (!executor.awaitTermination(5, TimeUnit.SECONDS))  {
            LOGGER.info("Forcing execution listener shutdown...");
            executor.shutdownNow();
        }
        executionRequestMonitorThread.join(2000);
        LOGGER.info("Execution listener shutdown");
        Thread mainThread = Thread.currentThread();
        mainThread.join(2000);
    }

}
