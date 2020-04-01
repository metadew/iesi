package io.metadew.iesi.runtime;

import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.execution.ExecutionRequestConfiguration;
import io.metadew.iesi.metadata.configuration.execution.script.ScriptExecutionRequestConfiguration;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequestStatus;
import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestKey;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequestStatus;
import io.metadew.iesi.metadata.definition.key.MetadataKey;
import lombok.extern.log4j.Log4j2;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Log4j2
public class ExecutionRequestMonitor implements Runnable {

    private final Long timeout;
    private final Object lock = new Object();
    private ConcurrentHashMap<ExecutionRequestKey, ThreadTimeCombination> executionRequestThreadMap;
    private boolean keepRunning = true;

    private static ExecutionRequestMonitor INSTANCE;

    public static ExecutionRequestMonitor getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ExecutionRequestMonitor();
        }
        return INSTANCE;
    }

    private ExecutionRequestMonitor() {
        // TODO: Create server configuration
        this.timeout = Configuration.getInstance()
                .getProperty("iesi.server.threads.timeout")
                .map(settingPath -> new Long((Integer) settingPath))
                .orElse(60L);
        this.executionRequestThreadMap = new ConcurrentHashMap<>();
    }

    @Override
    public void run() {
        while (keepRunning) {
            try {
                checkExecutionRequests();
                Thread.sleep(10000);
            } catch (InterruptedException ignored) {
            } catch (Exception e) {
                StringWriter stackTrace = new StringWriter();
                e.printStackTrace(new PrintWriter(stackTrace));
                log.info("exception=" + e);
                log.debug("exception.stacktrace=" + stackTrace.toString());
            }
        }
    }

    public void monitor(ExecutionRequestKey executionRequestKey, Thread task) {
        if (!ExecutionRequestConfiguration.getInstance().exists(executionRequestKey)) {
            log.warn(MessageFormat.format("executionrequestmonitor=could not find execution request {0} in repository, removing from watch list", executionRequestKey.toString()));
        } else {
            synchronized (lock) {
                log.debug(MessageFormat.format("executionrequestmonitor=start monitoring execution request {0}", executionRequestKey.toString()));
                executionRequestThreadMap.put(executionRequestKey, new ThreadTimeCombination(task, LocalDateTime.now()));
            }
        }
    }

    public void stopMonitoring(ExecutionRequestKey executionRequestKey) {
        synchronized (lock) {
            log.debug(MessageFormat.format("executionrequestmonitor=stop monitoring execution request {0}", executionRequestKey.toString()));
            executionRequestThreadMap.remove(executionRequestKey);
        }
    }

    private void checkExecutionRequests() {
        synchronized (lock) {
            log.debug(MessageFormat.format("executionrequestmonitor=({0}) still executing", executionRequestThreadMap.keySet().stream()
                    .map(MetadataKey::toString)
                    .collect(Collectors.joining(", "))));
            for (Map.Entry<ExecutionRequestKey, ThreadTimeCombination> executionRequestThreadEntry : executionRequestThreadMap.entrySet()) {
                if (isTerminated(executionRequestThreadEntry.getKey())) {
                    log.debug(MessageFormat.format("executionrequestmonitor=execution request {0} completed, remove from monitoring", executionRequestThreadEntry.toString()));
                    executionRequestThreadMap.remove(executionRequestThreadEntry.getKey());
                } else if (executionRequestThreadEntry.getValue().startTimestamp.plus(timeout, ChronoUnit.MINUTES).isBefore(LocalDateTime.now())) {
                    log.info(MessageFormat.format("executionrequestmonitor=execution request {0} exceeded timeout", executionRequestThreadEntry.toString()));
                    try {
                        executionRequestThreadEntry.getValue().thread.interrupt();
                        executionRequestThreadEntry.getValue().thread.join(5000);
                    } catch (InterruptedException e) {
                        log.warn(MessageFormat.format("executionrequestmonitor=unable to close blocking Execution Request {}", executionRequestThreadEntry.getKey().toString()));
                    }
                    ExecutionRequestConfiguration.getInstance().get(executionRequestThreadEntry.getKey())
                            .ifPresent(executionRequest -> {
                                executionRequest.getScriptExecutionRequests().forEach(this::markAborted);
                                ExecutionRequestConfiguration.getInstance().update(executionRequest);
                            });
                    executionRequestThreadMap.remove(executionRequestThreadEntry.getKey());
                }
            }
        }
    }

    private void markAborted(ScriptExecutionRequest scriptExecutionRequest) {
        scriptExecutionRequest.setScriptExecutionRequestStatus(ScriptExecutionRequestStatus.ABORTED);
    }

    private boolean isTerminated(ExecutionRequestKey executionRequestKey) {
        return ExecutionRequestConfiguration.getInstance().get(executionRequestKey)
                .map(ExecutionRequest::getExecutionRequestStatus)
                .map(executionRequestStatus -> executionRequestStatus.equals(ExecutionRequestStatus.COMPLETED) ||
                        executionRequestStatus.equals(ExecutionRequestStatus.DECLINED) ||
                        executionRequestStatus.equals(ExecutionRequestStatus.STOPPED) ||
                        executionRequestStatus.equals(ExecutionRequestStatus.KILLED))
                .orElse(true);
    }

    public void shutdown() throws InterruptedException {
        keepRunning = false;
        for (ExecutionRequestKey executionRequestKey : executionRequestThreadMap.keySet()) {
            ExecutionRequestConfiguration.getInstance().get(executionRequestKey)
                    .ifPresent(executionRequest -> {
                        executionRequest.setExecutionRequestStatus(ExecutionRequestStatus.KILLED);
                        ExecutionRequestConfiguration.getInstance().update(executionRequest);
                    });
            ScriptExecutionRequestConfiguration.getInstance().getByExecutionRequest(executionRequestKey)
                    .forEach(scriptExecutionRequest -> {
                        scriptExecutionRequest.setScriptExecutionRequestStatus(ScriptExecutionRequestStatus.ABORTED);
                        ScriptExecutionRequestConfiguration.getInstance().update(scriptExecutionRequest);
                    });
        }
        log.info("shutting down execution request execution monitor...");
        Thread mainThread = Thread.currentThread();
        mainThread.join(2000);
        log.info("Execution request execution monitor shutdown");
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
