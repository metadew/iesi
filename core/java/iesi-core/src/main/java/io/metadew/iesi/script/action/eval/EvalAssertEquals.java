package io.metadew.iesi.script.action.eval;

import io.metadew.iesi.datatypes.DataTypeHandler;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.script.action.ActionTypeExecution;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;

public class EvalAssertEquals extends ActionTypeExecution {

    // Parameters
    private ActionParameterOperation expectedValue;
    private ActionParameterOperation actualValue;

    public EvalAssertEquals(ExecutionControl executionControl,
                            ScriptExecution scriptExecution, ActionExecution actionExecution) {
        super(executionControl, scriptExecution, actionExecution);
    }

    public void prepare() {
        // Reset Parameters
        this.expectedValue = new ActionParameterOperation(getExecutionControl(),
                getActionExecution(), getActionExecution().getAction().getType(), "expected");
        this.actualValue = new ActionParameterOperation(getExecutionControl(),
                getActionExecution(), getActionExecution().getAction().getType(), "actual");

        // Get Parameters
        for (ActionParameter actionParameter : getActionExecution().getAction().getParameters()) {
            if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("expected")) {
                expectedValue.setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("actual")) {
                actualValue.setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            }
        }

        // Create parameter list
        getActionParameterOperationMap().put("expected", expectedValue);
        getActionParameterOperationMap().put("actual", actualValue);
    }

    protected boolean executeAction() throws InterruptedException {
        boolean evaluation = DataTypeHandler.getInstance().equals(expectedValue.getValue(), actualValue.getValue(), getExecutionControl().getExecutionRuntime());
        if (evaluation) {
            getActionExecution().getActionControl().increaseSuccessCount();
        } else {
            getActionExecution().getActionControl().logOutput("action.error", "Actual value '" + actualValue.getValue() + "' does not match '" + expectedValue.getValue() + "'");
            getActionExecution().getActionControl().increaseErrorCount();
        }
        return evaluation;
    }

}