package io.metadew.iesi.script.execution;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ActionControl {
    
    private static final Logger LOGGER = LogManager.getLogger();

    private ExecutionControl executionControl;
    private ActionExecution actionExecution;
    private ExecutionMetrics executionMetrics;
    private ActionRuntime actionRuntime;

    public ActionControl(ExecutionControl executionControl, ActionExecution actionExecution) {
        this.executionControl = executionControl;
        this.actionExecution = actionExecution;
        this.actionRuntime = new ActionRuntime(executionControl.getRunId(), actionExecution.getProcessId());
        this.executionMetrics = new ExecutionMetrics();
    }

    // Methods
    public void logOutput(String name, String value) {
        // Cache output
        this.actionRuntime.getRuntimeActionCacheConfiguration()
                .setRuntimeCache(executionControl.getRunId(), actionExecution.getProcessId(), "out", name, value);
        LOGGER.debug("action.output=" + name + ":" + value);
        executionControl.logExecutionOutput(actionExecution, name, value);
    }

    public void logError(String name, String value) {
        // Cache output
        this.actionRuntime.getRuntimeActionCacheConfiguration()
                .setRuntimeCache(executionControl.getRunId(),  actionExecution.getProcessId(), "err", name, value);
        LOGGER.debug("action.error=" + name + ":" + value);
        executionControl.logExecutionOutput(actionExecution, name, value);
    }

    public void logWarning(String name, String value) {
        // Cache output
        this.actionRuntime.getRuntimeActionCacheConfiguration()
                .setRuntimeCache(executionControl.getRunId(),  actionExecution.getProcessId(), "warn", name, value);
        LOGGER.debug("action.warning=" + name + ":" + value);
        executionControl.logExecutionOutput(actionExecution, name, value);
    }

    // Metrics
    public void increaseSuccessCount() {
        this.increaseSuccessCount(1);
    }

    public void increaseSuccessCount(long increase) {
        this.actionRuntime.getRuntimeActionCacheConfiguration()
                .setRuntimeCache(executionControl.getRunId(),  actionExecution.getProcessId(), "sys", "rc", "0");
        executionMetrics.increaseSuccessCount(increase);
    }

    public void increaseWarningCount() {
        this.increaseWarningCount(1);
    }

    public void increaseWarningCount(long increase) {
        this.actionRuntime.getRuntimeActionCacheConfiguration() .setRuntimeCache(executionControl.getRunId(),  actionExecution.getProcessId(), "sys", "rc", "2");
        executionMetrics.increaseWarningCount(increase);
    }

    public void increaseErrorCount() {
        this.increaseErrorCount(1);
    }

    public void increaseErrorCount(long increase) {
        this.actionRuntime.getRuntimeActionCacheConfiguration().setRuntimeCache(executionControl.getRunId(),  actionExecution.getProcessId(), "sys", "rc", "1");
        executionMetrics.increaseErrorCount(increase);
    }

    public void increaseSkipCount() {
        this.increaseSkipCount(1);
    }

    public void increaseSkipCount(long increase) {
        this.actionRuntime.getRuntimeActionCacheConfiguration()
                .setRuntimeCache(executionControl.getRunId(),  actionExecution.getProcessId(), "sys", "rc", "3");
        executionMetrics.increaseSkipCount(increase);
    }

    // Getters and Setters
    public ActionRuntime getActionRuntime() {
        return actionRuntime;
    }

    public ExecutionMetrics getExecutionMetrics() {
        return executionMetrics;
    }

}