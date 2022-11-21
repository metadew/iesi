package io.metadew.iesi.script.execution;

import io.metadew.iesi.metadata.configuration.action.performance.ActionPerformanceConfiguration;
import io.metadew.iesi.metadata.definition.action.performance.ActionPerformance;
import io.metadew.iesi.metadata.definition.action.performance.key.ActionPerformanceKey;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class ActionPerformanceLogger {

    private final ActionPerformanceConfiguration actionPerformanceConfiguration;

    public ActionPerformanceLogger(ActionPerformanceConfiguration actionPerformanceConfiguration) {
        this.actionPerformanceConfiguration = actionPerformanceConfiguration;
    }

    public void log(ActionExecution actionExecution, String scope, LocalDateTime startTimestamp, LocalDateTime endTimestamp) {
        actionPerformanceConfiguration.insert(new ActionPerformance(new ActionPerformanceKey(actionExecution.getExecutionControl().getRunId(), actionExecution.getProcessId(), scope),
                actionExecution.getExecutionControl().getEnvName(), actionExecution.getAction().getMetadataKey().getActionId(), startTimestamp, endTimestamp, (double) Duration.between(startTimestamp, endTimestamp).toMillis()));
    }

}
