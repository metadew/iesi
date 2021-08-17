package io.metadew.iesi.script.action.fwk;

import io.metadew.iesi.script.action.ActionTypeExecution;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;

public class FwkStopIteration extends ActionTypeExecution {

    // Parameters
    private static final String START_ACTION_NAME_KEY = "start_action_nm";

    // Constructors
    public FwkStopIteration(ExecutionControl executionControl, ScriptExecution scriptExecution, ActionExecution actionExecution) {
        super(executionControl, scriptExecution, actionExecution);
    }

    public void prepareAction() {

    }

    protected boolean executeAction() throws InterruptedException {

        // Run the action
        this.getActionExecution().getActionControl().increaseSuccessCount();

        return true;
    }

    @Override
    protected String getKeyword() {
        return "fwk.stopIteration";
    }

}