package io.metadew.iesi.script.execution;

import io.metadew.iesi.metadata.configuration.ActionPerformanceConfiguration;
import io.metadew.iesi.metadata.configuration.exception.ActionPerformanceAlreadyExistsException;
import io.metadew.iesi.metadata.definition.ActionPerformance;
import io.metadew.iesi.metadata.definition.key.ActionPerformanceKey;

import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;

public class ActionPerformanceLogger {

    private final ActionPerformanceConfiguration configuration;

    public ActionPerformanceLogger(ActionPerformanceConfiguration configuration) {
        this.configuration = configuration;
    }

    public void log(ActionExecution actionExecution, String scope, LocalDateTime startTimestamp, LocalDateTime endTimestalmp) {
        try {
            configuration.insert(new ActionPerformance(new ActionPerformanceKey(actionExecution.getExecutionControl().getRunId(), actionExecution.getProcessId(), actionExecution.getAction().getId(), scope),
                    actionExecution.getExecutionControl().getEnvName(), startTimestamp, endTimestalmp, (double) Duration.between(startTimestamp, endTimestalmp).toMillis()));
        } catch (SQLException | ActionPerformanceAlreadyExistsException e) {
            e.printStackTrace();
        }
    }

}
