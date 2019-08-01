package io.metadew.iesi.script.execution;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.service.action.ActionTraceService;
import io.metadew.iesi.metadata.service.script.ScriptTraceService;
import io.metadew.iesi.script.operation.ActionParameterOperation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for storing all trace information that is applicable during a script execution
 *
 * @author peter.billen
 */
public class ExecutionTrace {

    private ActionTraceService actionTraceService;
    private ScriptTraceService scriptTraceService;

    // Constructors
    public ExecutionTrace(FrameworkExecution frameworkExecution) {
        this.actionTraceService = new ActionTraceService(frameworkExecution.getMetadataControl());
        this.scriptTraceService = new ScriptTraceService(frameworkExecution.getMetadataControl());
    }

    // Insert
    public void setExecution(ScriptExecution scriptExecution) {
        scriptTraceService.trace(scriptExecution);
    }

    public void setExecution(ScriptExecution scriptExecution, ActionExecution actionExecution, HashMap<String, ActionParameterOperation> actionParameterOperationMap) {
        actionTraceService.trace(actionExecution, actionParameterOperationMap);
    }

}