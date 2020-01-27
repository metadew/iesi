package io.metadew.iesi.runtime;

import io.metadew.iesi.framework.configuration.FrameworkSettingConfiguration;
import io.metadew.iesi.framework.execution.FrameworkControl;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.configuration.execution.ExecutionRequestConfiguration;
import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestKey;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequestStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ExecutionRequestMonitor implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger();
    private final Long timeout;
    private Map<ExecutionRequestKey, ThreadTimeCombination> executionRequestThreadMap;
    private boolean keepRunning = true;

    private static ExecutionRequestMonitor INSTANCE;

    public static ExecutionRequestMonitor getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ExecutionRequestMonitor();
        }
        return INSTANCE;
    }

    private ExecutionRequestMonitor() {
        this.timeout = FrameworkSettingConfiguration.getInstance().getSettingPath("server.threads.timeout")
                .map(settingPath -> Long.parseLong(FrameworkControl.getInstance().getProperty(settingPath)))
                .orElse(60L);
        this.executionRequestThreadMap = new ConcurrentHashMap<>();
    }

    @Override
    public void run() {
        while (keepRunning) {
            checkExecutionRequests();
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ignored) {
            }
        }
    }

    public void monitor(ExecutionRequestKey executionRequestKey, Thread task) {
        LOGGER.debug(MessageFormat.format("executionrequestmonitor=monitoring execution request {0}", executionRequestKey.toString()));
        if (!ExecutionRequestConfiguration.getInstance().exists(executionRequestKey)) {
            LOGGER.warn(MessageFormat.format("executionrequestmonitor=could not find execution request {0} in repository, removing from watch list", executionRequestKey.toString()));
        } else {
            synchronized (executionRequestThreadMap) {
                executionRequestThreadMap.put(executionRequestKey, new ThreadTimeCombination(task, LocalDateTime.now()));
            }
        }
    }

    private void checkExecutionRequests() {
        synchronized (executionRequestThreadMap) {
            for (Map.Entry<ExecutionRequestKey, ThreadTimeCombination> executionRequestThreadEntry : executionRequestThreadMap.entrySet()) {
                if (executionRequestThreadEntry.getValue().thread.isAlive()) {
                    if (executionRequestThreadEntry.getValue().startTimestamp.plus(timeout, ChronoUnit.MINUTES).isBefore(LocalDateTime.now())) {
                        LOGGER.info(MessageFormat.format("executionrequestmonitor=execution request {0} exceeded timeout", executionRequestThreadEntry.toString()));
                        try {
                            executionRequestThreadEntry.getValue().thread.interrupt();
                            executionRequestThreadEntry.getValue().thread.join(5000);
                            ExecutionRequestConfiguration.getInstance().get(executionRequestThreadEntry.getKey())
                                    .ifPresent(executionRequest -> {
                                        executionRequest.getScriptExecutionRequests().forEach(this::markAborted);
                                        try {
                                            ExecutionRequestConfiguration.getInstance().update(executionRequest);
                                        } catch (MetadataDoesNotExistException ignored) {}
                                    });
                            executionRequestThreadMap.remove(executionRequestThreadEntry.getKey());
                        } catch (InterruptedException e) {
                            LOGGER.warn(MessageFormat.format("executionrequestmonitor=unable to close blocking Execution Request {}", executionRequestThreadEntry.getKey().toString()));
                        }
                    }
                } else {
                    LOGGER.debug(MessageFormat.format("executionrequestmonitor=execution request {0} completed, remove from monitoring", executionRequestThreadEntry.toString()));
                    executionRequestThreadMap.remove(executionRequestThreadEntry.getKey());
                }
            }
        }
    }

    private void markAborted(ScriptExecutionRequest scriptExecutionRequest) {
        if (scriptExecutionRequest.getScriptExecutionRequestStatus().equals(ScriptExecutionRequestStatus.NEW) ||
                scriptExecutionRequest.getScriptExecutionRequestStatus().equals(ScriptExecutionRequestStatus.ACCEPTED) ||
                scriptExecutionRequest.getScriptExecutionRequestStatus().equals(ScriptExecutionRequestStatus.SUBMITTED)) {
            scriptExecutionRequest.updateScriptExecutionRequestStatus(ScriptExecutionRequestStatus.ABORTED);
        }
    }

    public void shutdown() throws InterruptedException {
        keepRunning = false;
        LOGGER.info("shutting down execution request execution monitor...");
        Thread mainThread = Thread.currentThread();
        mainThread.join(2000);
        LOGGER.info("Execution request execution monitor shutdown");
    }

    private static class ThreadTimeCombination {
        private final Thread thread;
        private final LocalDateTime startTimestamp;

        ThreadTimeCombination(Thread thread, LocalDateTime startTimestamp) {
            this.thread = thread;
            this.startTimestamp = startTimestamp;
        }
    }
}
