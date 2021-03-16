package io.metadew.iesi.script.action.fwk;

import io.metadew.iesi.script.action.ActionTypeExecution;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;

public class FwkStartIteration extends ActionTypeExecution {

    private static final String TYPE_NAME_KEY = "TYPE_NM";
    private static final String LIST_NAME_KEY = "LIST_NM";
    private static final String LIST_VALUES_KEY = "LIST_VAL";
    private static final String NUMBER_FROM_KEY = "NUMBER_FROM";
    private static final String NUMBER_TO_KEY = "NUMBER_TO";
    private static final String NUMBER_ACTION_KEY = "NUMBER_STEP";
    private static final String BREAK_ON_ERROR_KEY = "BREAK_ON_ERROR";

    // Constructors
    public FwkStartIteration(ExecutionControl executionControl, ScriptExecution scriptExecution, ActionExecution actionExecution) {
        super(executionControl, scriptExecution, actionExecution);
    }

    public void prepare() {

    }

    protected boolean executeAction() throws InterruptedException {
        this.getActionExecution().getActionControl().increaseSuccessCount();

        return true;
    }

    @Override
    protected String getKeyword() {
        return "fwk.startIteration";
    }
}