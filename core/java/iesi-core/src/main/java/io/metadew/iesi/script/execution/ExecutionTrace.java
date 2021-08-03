package io.metadew.iesi.script.execution;

import io.metadew.iesi.metadata.service.action.ActionTraceService;
import io.metadew.iesi.metadata.service.script.ScriptTraceService;
import io.metadew.iesi.script.action.ActionParameterResolvement;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Class for storing all trace information that is applicable during a script execution
 *
 * @author peter.billen
 */
public class ExecutionTrace {

    private static ExecutionTrace instance;

    public static ExecutionTrace getInstance() {
        if (instance == null) {
            instance = new ExecutionTrace();
        }
        return instance;
    }

    private ExecutionTrace() {
    }

    // Insert
    public void setExecution(ScriptExecution scriptExecution) {
        ScriptTraceService.getInstance().trace(scriptExecution);
    }

    public void setExecution(ActionExecution actionExecution, List<ActionParameterResolvement> actionParameterOperationMap) {
        ActionTraceService.getInstance().trace(actionExecution,
                actionParameterOperationMap.stream()
                .collect(Collectors.toMap(
                        entry -> entry.getActionParameter().getMetadataKey().getParameterName(),
                        ActionParameterResolvement::getResolvedValue)));
    }

}