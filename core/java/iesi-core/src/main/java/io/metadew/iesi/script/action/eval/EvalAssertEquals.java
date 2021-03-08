package io.metadew.iesi.script.action.eval;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.DataTypeHandler;
import io.metadew.iesi.datatypes.dataset.Dataset;
import io.metadew.iesi.datatypes.template.TemplateService;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.metadata.definition.template.Template;
import io.metadew.iesi.script.action.ActionTypeExecution;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;

public class EvalAssertEquals extends ActionTypeExecution {

    private static final String EXPECTED_KEY = "expected";
    private static final String ACTUAL_KEY = "actual";

    // Parameters
    private DataType expectedValue;
    private DataType actualValue;

    public EvalAssertEquals(ExecutionControl executionControl,
                            ScriptExecution scriptExecution, ActionExecution actionExecution) {
        super(executionControl, scriptExecution, actionExecution);
    }

    public void prepare() {
        // Reset Parameters
        ActionParameterOperation expectedValueActionParameterOperation = new ActionParameterOperation(getExecutionControl(),
                getActionExecution(), getActionExecution().getAction().getType(), EXPECTED_KEY);
        ActionParameterOperation actualValueActionParameterOperation = new ActionParameterOperation(getExecutionControl(),
                getActionExecution(), getActionExecution().getAction().getType(), ACTUAL_KEY);

        // Get Parameters
        for (ActionParameter actionParameter : getActionExecution().getAction().getParameters()) {
            if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase(EXPECTED_KEY)) {
                expectedValueActionParameterOperation.setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase(ACTUAL_KEY)) {
                actualValueActionParameterOperation.setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            }
        }

        // Create parameter list
        getActionParameterOperationMap().put(EXPECTED_KEY, expectedValueActionParameterOperation);
        getActionParameterOperationMap().put(ACTUAL_KEY, actualValueActionParameterOperation);

        expectedValue = expectedValueActionParameterOperation.getValue();
        actualValue = actualValueActionParameterOperation.getValue();

    }

    protected boolean executeAction() throws InterruptedException {
        boolean evaluation;
        if (expectedValue instanceof Template && actualValue instanceof Dataset) {
            evaluation = TemplateService.getInstance().matches(actualValue, (Template) expectedValue, getExecutionControl().getExecutionRuntime());
        } else {
            evaluation = DataTypeHandler.getInstance().equals(expectedValue, actualValue, getExecutionControl().getExecutionRuntime());
        }

        if (evaluation) {
            getActionExecution().getActionControl().increaseSuccessCount();
        } else {
            getActionExecution().getActionControl().logOutput("action.error", "Actual value '" + actualValue + "' does not match '" + expectedValue + "'");
            getActionExecution().getActionControl().increaseErrorCount();
        }
        return evaluation;
    }

    @Override
    protected String getKeyword() {
        return "eval.assertEquals";
    }

}