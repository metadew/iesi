package io.metadew.iesi.script.execution;

import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.script.operation.ActionSelectOperation;
import org.apache.logging.log4j.Level;

import java.util.Map;

public class NonRouteScriptExecution extends ScriptExecution {

    public NonRouteScriptExecution(Script script, String environment, ExecutionControl executionControl, ExecutionMetrics executionMetrics, Long processId, boolean exitOnCompletion, ScriptExecution parentScriptExecution, Map<String, String> parameters, Map<String, String> impersonations, ActionSelectOperation actionSelectOperation, RootingStrategy rootingStrategy) {
        super(script, environment, executionControl, executionMetrics, processId, exitOnCompletion, parentScriptExecution, parameters, impersonations, actionSelectOperation, rootingStrategy);
    }

    @Override
    public void prepareExecution() {
        getExecutionControl().logMessage("script.name=" + this.getScript().getName(), Level.INFO);
        getExecutionControl().logMessage("script.version=" + this.getScript().getVersion().getNumber(), Level.INFO);
        getExecutionControl().logMessage("exec.env=" + this.getExecutionControl().getEnvName(),
                Level.INFO);
        this.getExecutionControl().logStart(this);

        for (Map.Entry<String, String> parameter : parameters.entrySet()) {
            getExecutionControl().getExecutionRuntime().setRuntimeVariable(this, parameter.getKey(), parameter.getValue());
        }

        this.traceDesignMetadata();
    }

    @Override
    protected void endExecution() {
        setResult(getExecutionControl().logEnd(this));
        getRootingStrategy().endExecution(this);
    }

}
