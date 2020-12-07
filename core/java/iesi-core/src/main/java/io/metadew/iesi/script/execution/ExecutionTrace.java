package io.metadew.iesi.script.execution;

import io.metadew.iesi.metadata.service.action.ActionTraceService;
import io.metadew.iesi.metadata.service.script.ScriptTraceService;
import io.metadew.iesi.script.operation.ActionParameterOperation;

import java.util.HashMap;
import java.util.Map;
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

    public void setExecution(ActionExecution actionExecution, Map<String, ActionParameterOperation> actionParameterOperationMap) {
        ActionTraceService.getInstance().trace(actionExecution, actionParameterOperationMap.entrySet().stream()
                .filter(entry -> entry.getValue().getValue() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getValue())));
    }

}