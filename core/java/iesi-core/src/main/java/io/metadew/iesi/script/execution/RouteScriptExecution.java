package io.metadew.iesi.script.execution;

import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.script.operation.ActionSelectOperation;

import java.util.Map;

public class RouteScriptExecution extends ScriptExecution {

    public RouteScriptExecution(Script script, String environment, ExecutionControl executionControl, ExecutionMetrics executionMetrics, Long processId, boolean exitOnCompletion, ScriptExecution parentScriptExecution, Map<String, String> parameters, Map<String, String> impersonations, ActionSelectOperation actionSelectOperation, RootingStrategy rootingStrategy) {
        super(script, environment, executionControl, executionMetrics, processId, exitOnCompletion, parentScriptExecution, parameters, impersonations, actionSelectOperation, rootingStrategy);
    }

    @Override
    protected void endExecution() {

    }

    @Override
    protected void prepareExecution() {

    }
}
