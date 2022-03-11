package io.metadew.iesi.script.execution;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes._null.Null;
import io.metadew.iesi.datatypes.array.Array;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementation;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationHandler;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.definition.template.Template;
import io.metadew.iesi.metadata.definition.template.matcher.Matcher;
import io.metadew.iesi.metadata.definition.template.matcher.value.MatcherAnyValue;
import io.metadew.iesi.metadata.definition.template.matcher.value.MatcherFixedValue;
import io.metadew.iesi.metadata.definition.template.matcher.value.MatcherTemplate;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;
import java.util.Map;

@Log4j2
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

    public void logOutput(String name, DataType value) {
        // Cache output
        this.actionRuntime.getRuntimeActionCacheConfiguration()
                .setRuntimeCache(executionControl.getRunId(), actionExecution.getProcessId(), "out", name, value.toString());
        logOutputPerDatatype(name, value);
    }


    private void logOutputPerDatatype(String key, DataType value) {
        if (value == null || value instanceof Null) {
            executionControl.logExecutionOutput(actionExecution, key, "<null>");
        } else if (value instanceof Text) {
            executionControl.logExecutionOutput(actionExecution, key, ((Text) value).getString());
        } else if (value instanceof Array) {
            int counter = 0;
            for (DataType element : ((Array) value).getList()) {
                logOutputPerDatatype(key + counter + ".", element);
                counter++;
            }
        } else if (value instanceof DatasetImplementation) {
            for (Map.Entry<String, DataType> datasetItem : DatasetImplementationHandler.getInstance().getDataItems((DatasetImplementation) value, actionExecution.getExecutionControl().getExecutionRuntime()).entrySet()) {
                logOutputPerDatatype(key + datasetItem.getKey() + ".", datasetItem.getValue());
            }
        } else if (value instanceof Template) {
            for (Matcher matcher : ((Template) value).getMatchers()) {
                String matcherKey = key + "." + matcher.getKey();
                if (matcher.getMatcherValue() instanceof MatcherAnyValue) {
                    executionControl.logExecutionOutput(actionExecution, matcherKey + ".type", "any");
                } else if (matcher.getMatcherValue() instanceof MatcherFixedValue) {
                    executionControl.logExecutionOutput(actionExecution, matcherKey + ".type", "fixed");
                    executionControl.logExecutionOutput(actionExecution, matcherKey + ".value", ((MatcherFixedValue) matcher.getMatcherValue()).getValue());
                } else if (matcher.getMatcherValue() instanceof MatcherTemplate) {
                    executionControl.logExecutionOutput(actionExecution, matcherKey + ".type", "template");
                    executionControl.logExecutionOutput(actionExecution, matcherKey + ".template_name", ((MatcherTemplate) matcher.getMatcherValue()).getTemplateName());
                    executionControl.logExecutionOutput(actionExecution, matcherKey + ".template_version", ((MatcherTemplate) matcher.getMatcherValue()).getTemplateVersion().toString());
                } else {
                    log.warn("Cannot output MatcherValue of type " + matcher.getMatcherValue().getClass().getSimpleName());
                }
            }
        } else {
            LOGGER.warn(MessageFormat.format("DataType ''{0}'' is unknown to trace", value.getClass()));
        }
    }

    public void logError(String name, String value) {
        // Cache output
        this.actionRuntime.getRuntimeActionCacheConfiguration()
                .setRuntimeCache(executionControl.getRunId(), actionExecution.getProcessId(), "err", name, value);
        LOGGER.debug("action.error=" + name + ":" + value);
        executionControl.logExecutionOutput(actionExecution, name, value);
    }

    public void logWarning(String name, String value) {
        // Cache output
        this.actionRuntime.getRuntimeActionCacheConfiguration()
                .setRuntimeCache(executionControl.getRunId(), actionExecution.getProcessId(), "warn", name, value);
        LOGGER.debug("action.warning=" + name + ":" + value);
        executionControl.logExecutionOutput(actionExecution, name, value);
    }

    // Metrics
    public void increaseSuccessCount() {
        this.increaseSuccessCount(1);
    }

    public void increaseSuccessCount(long increase) {
        this.actionRuntime.getRuntimeActionCacheConfiguration()
                .setRuntimeCache(executionControl.getRunId(), actionExecution.getProcessId(), "sys", "rc", "0");
        executionMetrics.increaseSuccessCount(increase);
    }

    public void increaseWarningCount() {
        this.increaseWarningCount(1);
    }

    public void increaseWarningCount(long increase) {
        this.actionRuntime.getRuntimeActionCacheConfiguration().setRuntimeCache(executionControl.getRunId(), actionExecution.getProcessId(), "sys", "rc", "2");
        executionMetrics.increaseWarningCount(increase);
    }

    public void increaseErrorCount() {
        this.increaseErrorCount(1);
    }

    public void increaseErrorCount(long increase) {
        this.actionRuntime.getRuntimeActionCacheConfiguration().setRuntimeCache(executionControl.getRunId(), actionExecution.getProcessId(), "sys", "rc", "1");
        executionMetrics.increaseErrorCount(increase);
    }

    public void increaseSkipCount() {
        this.increaseSkipCount(1);
    }

    public void increaseSkipCount(long increase) {
        this.actionRuntime.getRuntimeActionCacheConfiguration()
                .setRuntimeCache(executionControl.getRunId(), actionExecution.getProcessId(), "sys", "rc", "3");
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