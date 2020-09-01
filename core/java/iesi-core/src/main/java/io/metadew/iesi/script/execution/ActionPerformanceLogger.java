package io.metadew.iesi.script.execution;

import io.metadew.iesi.metadata.configuration.action.performance.ActionPerformanceConfiguration;
import io.metadew.iesi.metadata.definition.action.performance.ActionPerformance;
import io.metadew.iesi.metadata.definition.action.performance.key.ActionPerformanceKey;

import java.time.Duration;
import java.time.LocalDateTime;

public class ActionPerformanceLogger {

    private static ActionPerformanceLogger INSTANCE;

    public static ActionPerformanceLogger getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ActionPerformanceLogger();
        }
        return INSTANCE;
    }

    private ActionPerformanceLogger() {
    }

    public void log(ActionExecution actionExecution, String scope, LocalDateTime startTimestamp, LocalDateTime endTimestamp) {
        ActionPerformanceConfiguration.getInstance().insert(new ActionPerformance(new ActionPerformanceKey(actionExecution.getExecutionControl().getRunId(), actionExecution.getProcessId(), scope),
                actionExecution.getExecutionControl().getEnvName(), actionExecution.getAction().getMetadataKey().getActionId(), startTimestamp, endTimestamp, (double) Duration.between(startTimestamp, endTimestamp).toMillis()));
    }

}
