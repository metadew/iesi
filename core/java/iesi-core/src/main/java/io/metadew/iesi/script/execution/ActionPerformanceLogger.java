package io.metadew.iesi.script.execution;

import io.metadew.iesi.metadata.configuration.action.performance.ActionPerformanceConfiguration;
import io.metadew.iesi.metadata.configuration.action.performance.exception.ActionPerformanceAlreadyExistsException;
import io.metadew.iesi.metadata.definition.action.performance.ActionPerformance;
import io.metadew.iesi.metadata.definition.action.performance.key.ActionPerformanceKey;

import java.time.Duration;
import java.time.LocalDateTime;

public class ActionPerformanceLogger {


    public ActionPerformanceLogger() {}

    public void log(ActionExecution actionExecution, String scope, LocalDateTime startTimestamp, LocalDateTime endTimestalmp) {
        try {
            ActionPerformanceConfiguration.getInstance().insert(new ActionPerformance(new ActionPerformanceKey(actionExecution.getExecutionControl().getRunId(), actionExecution.getProcessId(), scope),
                    actionExecution.getExecutionControl().getEnvName(), actionExecution.getAction().getId(), startTimestamp, endTimestalmp, (double) Duration.between(startTimestamp, endTimestalmp).toMillis()));
        } catch (ActionPerformanceAlreadyExistsException e) {
            e.printStackTrace();
        }
    }

}
