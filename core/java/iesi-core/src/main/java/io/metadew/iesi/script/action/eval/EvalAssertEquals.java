package io.metadew.iesi.script.action.eval;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.script.action.ActionTypeExecution;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;
import java.util.HashMap;

public class EvalAssertEquals extends ActionTypeExecution {


    // Parameters
    private ActionParameterOperation expectedValue;
    private ActionParameterOperation actualValue;
    private static final Logger LOGGER = LogManager.getLogger();

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
        String expected = convertExpectedValue(expectedValue.getValue());
        String actual = convertActualValue(actualValue.getValue());
        boolean evaluation = expectedValue.equals(actualValue);
        if (evaluation) {
            getActionExecution().getActionControl().increaseSuccessCount();
        } else {
            getActionExecution().getActionControl().increaseErrorCount();
        }
        return evaluation;
    }

    private String convertActualValue(DataType actualValue) {
        if (actualValue instanceof Text) {
            return actualValue.toString();
        } else {
            LOGGER.warn(MessageFormat.format(getActionExecution().getAction().getType() + " does not accept {0} as type for actualValue",
                    actualValue.getClass()));
            return actualValue.toString();
        }
    }

    private String convertExpectedValue(DataType expectedValue) {
        if (expectedValue instanceof Text) {
            return expectedValue.toString();
        } else {
            LOGGER.warn(MessageFormat.format(getActionExecution().getAction().getType() + " does not accept {0} as type for expectedValue",
                    expectedValue.getClass()));
            return expectedValue.toString();
        }
    }

    public ActionParameterOperation getExpectedValue() {
        return expectedValue;
    }

    public void setExpectedValue(ActionParameterOperation expectedValue) {
        this.expectedValue = expectedValue;
    }

    public ActionParameterOperation getActualValue() {
        return actualValue;
    }

    public void setActualValue(ActionParameterOperation actualValue) {
        this.actualValue = actualValue;
    }

}