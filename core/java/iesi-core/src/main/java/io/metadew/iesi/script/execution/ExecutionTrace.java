package io.metadew.iesi.script.execution;

import io.metadew.iesi.metadata.service.action.ActionTraceService;
import io.metadew.iesi.metadata.service.script.ScriptTraceService;
import io.metadew.iesi.script.action.ActionParameterResolvement;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Class for storing all trace information that is applicable during a script execution
 *
 * @author peter.billen
 */
@Component
public class ExecutionTrace {

    private final ScriptTraceService scriptTraceService;
    private final ActionTraceService actionTraceService;

    public ExecutionTrace(ScriptTraceService scriptTraceService, ActionTraceService actionTraceService) {
        this.scriptTraceService = scriptTraceService;
        this.actionTraceService = actionTraceService;
    }


    // Insert
    public void setExecution(ScriptExecution scriptExecution) {
        scriptTraceService.trace(scriptExecution);
    }

    public void setExecution(ActionExecution actionExecution, List<ActionParameterResolvement> actionParameterOperationMap) {
        actionTraceService.trace(actionExecution,
                actionParameterOperationMap.stream()
                .collect(Collectors.toMap(
                        entry -> entry.getActionParameter().getMetadataKey().getParameterName(),
                        ActionParameterResolvement::getResolvedValue)));
    }

}