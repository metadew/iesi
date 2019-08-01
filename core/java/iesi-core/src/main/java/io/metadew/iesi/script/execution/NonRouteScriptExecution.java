package io.metadew.iesi.script.execution;

import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.script.operation.ActionSelectOperation;
import org.apache.logging.log4j.Level;

public class NonRouteScriptExecution extends ScriptExecution {

    public NonRouteScriptExecution(Script script, FrameworkExecution frameworkExecution, ExecutionControl executionControl, ExecutionMetrics executionMetrics, Long processId, boolean exitOnCompletion, ScriptExecution parentScriptExecution, String paramList, String paramFile, ActionSelectOperation actionSelectOperation, RootingStrategy rootingStrategy) {
        super(script, frameworkExecution, executionControl, executionMetrics, processId, exitOnCompletion, parentScriptExecution, paramList, paramFile, actionSelectOperation, rootingStrategy);
    }

    @Override
    public void prepareExecution() {
        getExecutionControl().logMessage(this, "script.name=" + this.getScript().getName(), Level.INFO);
        getExecutionControl().logMessage(this, "script.version=" + this.getScript().getVersion().getNumber(), Level.INFO);
        getExecutionControl().logMessage(this, "exec.env=" + this.getExecutionControl().getEnvName(),
                Level.INFO);
        this.getExecutionControl().logStart(this);

        /*
         * Initialize parameters. A parameter file has priority over a parameter list
         */
        if (!this.getParamFile().trim().equals("")) {
            this.getExecutionControl().getExecutionRuntime().loadParamFiles(this, this.getParamFile());
        }
        if (!this.getParamList().trim().equals("")) {
            this.getExecutionControl().getExecutionRuntime().loadParamList(this, this.getParamList());
        }

        /*
         * Perform trace of the script design configuration
         */
        this.traceDesignMetadata();
    }

    @Override
    protected void endExecution() {
        setResult(getExecutionControl().logEnd(this));
        getRootingStrategy().endExecution(this);
    }

}
