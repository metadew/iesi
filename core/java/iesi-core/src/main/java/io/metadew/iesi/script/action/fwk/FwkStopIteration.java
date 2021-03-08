package io.metadew.iesi.script.action.fwk;

import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.script.action.ActionTypeExecution;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;

public class FwkStopIteration extends ActionTypeExecution {

    // Parameters
    private ActionParameterOperation startActionName;

    // Constructors
    public FwkStopIteration(ExecutionControl executionControl, ScriptExecution scriptExecution, ActionExecution actionExecution) {
        super(executionControl, scriptExecution, actionExecution);
    }

    public void prepare() {
        // Reset Parameters
        this.setStartActionName(new ActionParameterOperation(this.getExecutionControl(), this.getActionExecution(),
                this.getActionExecution().getAction().getType(), "START_STEP_NM"));

        // Get Parameters
        for (ActionParameter actionParameter : this.getActionExecution().getAction().getParameters()) {
            if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("start_action_nm")) {
                this.getStartActionName().setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            }
        }

        //Create parameter list
        this.getActionParameterOperationMap().put("START_STEP_NM", this.getStartActionName());
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

    public ActionParameterOperation getStartActionName() {
        return startActionName;
    }

    public void setStartActionName(ActionParameterOperation startActionName) {
        this.startActionName = startActionName;
    }


}