package io.metadew.iesi.script.execution;

import io.metadew.iesi.metadata.configuration.environment.EnvironmentParameterConfiguration;
import io.metadew.iesi.metadata.definition.action.Action;
import io.metadew.iesi.metadata.definition.environment.key.EnvironmentKey;
import io.metadew.iesi.script.operation.ActionSelectOperation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;

@Component
public class RootStrategy implements RootingStrategy {


    private static final Logger LOGGER = LogManager.getLogger();
    private final EnvironmentParameterConfiguration environmentParameterConfiguration;

    public RootStrategy(EnvironmentParameterConfiguration environmentParameterConfiguration) {
        this.environmentParameterConfiguration = environmentParameterConfiguration;
    }


    @Override
    public void prepareExecution(ScriptExecution scriptExecution) {
        environmentParameterConfiguration
                .getByEnvironment(new EnvironmentKey(scriptExecution.getEnvironment()))
                .forEach(environmentParameter -> scriptExecution.getExecutionControl().getExecutionRuntime()
                        .setRuntimeVariable(
                                scriptExecution,
                                environmentParameter.getName(),
                                environmentParameter.getValue()));
    }

    @Override
    public boolean executionAllowed(ActionSelectOperation actionSelectOperation, Action action) {
        boolean actionAllowed = actionSelectOperation.getExecutionStatus(action);
        LOGGER.trace(MessageFormat.format("Execution of action ''{0}'' is {1}allowed", action.getName(), (actionAllowed ? "" : "not ")));
        return actionSelectOperation.getExecutionStatus(action);
    }

    @Override
    public void endExecution(ScriptExecution scriptExecution) {
        scriptExecution.getExecutionControl().terminate();
        if (scriptExecution.isExitOnCompletion()) {
            scriptExecution.getExecutionControl().endExecution();
        }
        scriptExecution.getExecutionControl().getExecutionRuntime().getRuntimeVariableConfiguration().shutdown();
    }

    @Override
    public void continueAction(ActionSelectOperation actionSelectOperation, Action action) {
        actionSelectOperation.setContinueStatus(action);
    }


}
