package io.metadew.iesi.script.action.eval;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.HashMap;

public class EvalAssertEquals {

    private ActionExecution actionExecution;
    private ExecutionControl executionControl;

    // Parameters
    private ActionParameterOperation expectedValue;
    private ActionParameterOperation actualValue;
    private HashMap<String, ActionParameterOperation> actionParameterOperationMap;
    private static final Logger LOGGER = LogManager.getLogger();

    public EvalAssertEquals(ExecutionControl executionControl,
                            ScriptExecution scriptExecution, ActionExecution actionExecution) {
        this.executionControl = executionControl;
        this.actionExecution = actionExecution;
        this.actionParameterOperationMap = new HashMap<>();
    }

    public void prepare()  {
        // Reset Parameters
        this.expectedValue = new ActionParameterOperation(executionControl,
                actionExecution, actionExecution.getAction().getType(), "expected");
        this.actualValue = new ActionParameterOperation(executionControl,
                actionExecution, actionExecution.getAction().getType(), "actual");

        // Get Parameters
        for (ActionParameter actionParameter : actionExecution.getAction().getParameters()) {
            if (actionParameter.getName().equalsIgnoreCase("expected")) {
                expectedValue.setInputValue(actionParameter.getValue());
            } else if (actionParameter.getName().equalsIgnoreCase("actual")) {
                actualValue.setInputValue(actionParameter.getValue());
            }
        }

        // Create parameter list
        actionParameterOperationMap.put("expected", expectedValue);
        actionParameterOperationMap.put("actual", actualValue);
    }

    public boolean execute() {
        try {
            String expected = convertExpectedValue(expectedValue.getValue());
            String actual = convertActualValue(actualValue.getValue());
            return compare(expected, actual);
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));

            actionExecution.getActionControl().increaseErrorCount();

            actionExecution.getActionControl().logOutput("exception", e.getMessage());
            actionExecution.getActionControl().logOutput("stacktrace", StackTrace.toString());

            return false;
        }

    }

    private boolean compare(String expectedValue, String actualValue) {
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
            LOGGER.warn(MessageFormat.format(actionExecution.getAction().getType() + " does not accept {0} as type for actualValue",
                    actualValue.getClass()));
            return actualValue.toString();
        }
    }

    private String convertExpectedValue(DataType expectedValue) {
        if (expectedValue instanceof Text) {
            return expectedValue.toString();
        } else {
            LOGGER.warn(MessageFormat.format(actionExecution.getAction().getType() + " does not accept {0} as type for expectedValue",
                    expectedValue.getClass()));
            return expectedValue.toString();
        }
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

    public HashMap<String, ActionParameterOperation> getActionParameterOperationMap() {
        return actionParameterOperationMap;
    }

    public void setActionParameterOperationMap(HashMap<String, ActionParameterOperation> actionParameterOperationMap) {
        this.actionParameterOperationMap = actionParameterOperationMap;
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