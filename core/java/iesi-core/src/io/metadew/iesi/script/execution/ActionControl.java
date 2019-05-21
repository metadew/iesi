package io.metadew.iesi.script.execution;

import io.metadew.iesi.framework.execution.FrameworkExecution;
import org.apache.logging.log4j.Level;

public class ActionControl {

    private ExecutionControl executionControl;
    private ActionExecution actionExecution;
    private ExecutionMetrics executionMetrics;
    private FrameworkExecution frameworkExecution;
    private ActionRuntime actionRuntime;

    public ActionControl(FrameworkExecution frameworkExecution, ExecutionControl executionControl, ActionExecution actionExecution) {
        this.setFrameworkExecution(frameworkExecution);
        this.setExecutionControl(executionControl);
        this.setActionExecution(actionExecution);
        this.setActionRuntime(new ActionRuntime(this.getFrameworkExecution(), this.getExecutionControl().getRunId(), this.getActionExecution().getProcessId()));
        this.setExecutionMetrics(new ExecutionMetrics());
    }

    // Methods
    public void logOutput(String name, String value) {
        // Cache output
        this.getActionExecution().getActionControl().getActionRuntime().getRuntimeActionCacheConfiguration()
                .setRuntimeCache(this.getExecutionControl().getRunId(), "out", name, value);
        this.getExecutionControl().logMessage(this.getActionExecution(), "action.output=" + name + ":" + value, Level.DEBUG);
        this.getExecutionControl().logExecutionOutput(this.getActionExecution(), name, value);
    }

    public void logError(String name, String value) {
        // Cache output
        this.getActionExecution().getActionControl().getActionRuntime().getRuntimeActionCacheConfiguration()
                .setRuntimeCache(this.getExecutionControl().getRunId(), "err", name, value);
        this.getExecutionControl().logMessage(this.getActionExecution(), "action.error=" + name + ":" + value, Level.DEBUG);
        this.getExecutionControl().logExecutionOutput(this.getActionExecution(), name, value);
    }

    public void logWarning(String name, String value) {
        // Cache output
        this.getActionExecution().getActionControl().getActionRuntime().getRuntimeActionCacheConfiguration()
                .setRuntimeCache(this.getExecutionControl().getRunId(), "warn", name, value);
        this.getExecutionControl().logMessage(this.getActionExecution(), "action.warning=" + name + ":" + value, Level.DEBUG);
        this.getExecutionControl().logExecutionOutput(this.getActionExecution(), name, value);
    }

    // Metrics
    public void increaseSuccessCount() {
        this.increaseSuccessCount(1);
    }

    public void increaseSuccessCount(long increase) {
        this.getActionExecution().getActionControl().getActionRuntime().getRuntimeActionCacheConfiguration()
                .setRuntimeCache(this.getExecutionControl().getRunId(), "sys", "rc", "0");
        this.getExecutionMetrics().increaseSuccessCount(increase);
    }

    public void increaseWarningCount() {
        this.increaseWarningCount(1);
    }

    public void increaseWarningCount(long increase) {
        this.getActionExecution().getActionControl().getActionRuntime().getRuntimeActionCacheConfiguration()
                .setRuntimeCache(this.getExecutionControl().getRunId(), "sys", "rc", "2");
        this.getExecutionMetrics().increaseWarningCount(increase);
    }

    public void increaseErrorCount() {
        this.increaseErrorCount(1);
    }

    public void increaseErrorCount(long increase) {
        this.getActionExecution().getActionControl().getActionRuntime().getRuntimeActionCacheConfiguration()
                .setRuntimeCache(this.getExecutionControl().getRunId(), "sys", "rc", "1");
        this.getExecutionMetrics().increaseErrorCount(increase);
    }

    public void increaseSkipCount() {
        this.increaseSkipCount(1);
    }

    public void increaseSkipCount(long increase) {
        this.getActionExecution().getActionControl().getActionRuntime().getRuntimeActionCacheConfiguration()
                .setRuntimeCache(this.getExecutionControl().getRunId(), "sys", "rc", "3");
        this.getExecutionMetrics().increaseSkipCount(increase);
    }

    // Getters and Setters
    public ActionRuntime getActionRuntime() {
        return actionRuntime;
    }

    public void setActionRuntime(ActionRuntime actionRuntime) {
        this.actionRuntime = actionRuntime;
    }

    public FrameworkExecution getFrameworkExecution() {
        return frameworkExecution;
    }

    public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
        this.frameworkExecution = frameworkExecution;
    }

    public ExecutionControl getExecutionControl() {
        return executionControl;
    }

    public void setExecutionControl(ExecutionControl executionControl) {
        this.executionControl = executionControl;
    }

    public ExecutionMetrics getExecutionMetrics() {
        return executionMetrics;
    }

    public void setExecutionMetrics(ExecutionMetrics executionMetrics) {
        this.executionMetrics = executionMetrics;
    }

    public ActionExecution getActionExecution() {
        return actionExecution;
    }

    public void setActionExecution(ActionExecution actionExecution) {
        this.actionExecution = actionExecution;
    }

}