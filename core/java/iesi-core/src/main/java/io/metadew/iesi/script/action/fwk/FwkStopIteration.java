package io.metadew.iesi.script.action.fwk;

import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.operation.ActionParameterOperation;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;

public class FwkStopIteration {

    private ActionExecution actionExecution;
    private ExecutionControl executionControl;

    // Parameters
    private ActionParameterOperation startActionName;
    private HashMap<String, ActionParameterOperation> actionParameterOperationMap;

    // Constructors
    public FwkStopIteration(ExecutionControl executionControl, ActionExecution actionExecution) {
        this.setExecutionControl(executionControl);
        this.setActionExecution(actionExecution);
        this.setActionParameterOperationMap(new HashMap<>());
    }

    public void prepare() {
        // Reset Parameters
        this.setStartActionName(new ActionParameterOperation(this.getExecutionControl(), this.getActionExecution(),
                this.getActionExecution().getAction().getType(), "START_STEP_NM"));

        // Get Parameters
        for (ActionParameter actionParameter : this.getActionExecution().getAction().getParameters()) {
            if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("start_action_nm")) {
                this.getStartActionName().setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            }
        }

        //Create parameter list
        this.getActionParameterOperationMap().put("START_STEP_NM", this.getStartActionName());
    }

    public boolean execute() throws InterruptedException {
        try {
            return performOperation();
        } catch (InterruptedException e) {
            throw (e);
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));

            this.getActionExecution().getActionControl().increaseErrorCount();

            this.getActionExecution().getActionControl().logOutput("exception", e.getMessage());
            this.getActionExecution().getActionControl().logOutput("stacktrace", StackTrace.toString());

            return false;
        }

    }

    private boolean performOperation() throws InterruptedException {

        // Run the action
        this.getActionExecution().getActionControl().increaseSuccessCount();

        return true;
    }

    public ExecutionControl getExecutionControl() {
        return executionControl;
    }

    public void setExecutionControl(ExecutionControl executionControl) {
        this.executionControl = executionControl;
    }

    public ActionExecution getActionExecution() {
        return actionExecution;
    }

    public void setActionExecution(ActionExecution actionExecution) {
        this.actionExecution = actionExecution;
    }

    public ActionParameterOperation getStartActionName() {
        return startActionName;
    }

    public void setStartActionName(ActionParameterOperation startActionName) {
        this.startActionName = startActionName;
    }

    public HashMap<String, ActionParameterOperation> getActionParameterOperationMap() {
        return actionParameterOperationMap;
    }

    public void setActionParameterOperationMap(HashMap<String, ActionParameterOperation> actionParameterOperationMap) {
        this.actionParameterOperationMap = actionParameterOperationMap;
    }

}