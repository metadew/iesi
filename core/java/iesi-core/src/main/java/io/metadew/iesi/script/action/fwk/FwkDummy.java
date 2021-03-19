package io.metadew.iesi.script.action.fwk;

import io.metadew.iesi.script.action.ActionTypeExecution;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import org.apache.logging.log4j.Level;

public class FwkDummy extends ActionTypeExecution {

    public FwkDummy(ExecutionControl executionControl, ScriptExecution scriptExecution, ActionExecution actionExecution) {
        super(executionControl, scriptExecution, actionExecution);
    }

    public void prepare() {

    }

    protected boolean executeAction() throws InterruptedException {
        this.getExecutionControl().logMessage("Not doing anything", Level.TRACE);
        this.getActionExecution().getActionControl().increaseSuccessCount();
        return true;
    }

    @Override
    protected String getKeyword() {
        return "fwk.dummy";
    }

}