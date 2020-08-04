package io.metadew.iesi.script.action;

import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;
import lombok.Getter;

import java.util.HashMap;

@Getter
public abstract class ActionTypeExecution {

    private final ExecutionControl executionControl;
    private final ScriptExecution scriptExecution;
    private final ActionExecution actionExecution;
    private HashMap<String, ActionParameterOperation> actionParameterOperationMap = new HashMap<>();

    public ActionTypeExecution(ExecutionControl executionControl,
                               ScriptExecution scriptExecution, ActionExecution actionExecution) {
        this.executionControl = executionControl;
        this.scriptExecution = scriptExecution;
        this.actionExecution = actionExecution;
    }

    public abstract void prepare();

    public abstract boolean execute() throws InterruptedException;



}
