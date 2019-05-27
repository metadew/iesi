package io.metadew.iesi.script.action;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.Text;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.ActionParameter;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;
import org.apache.logging.log4j.Level;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.HashMap;

public class EvalAssertEquals {

    private ActionExecution actionExecution;
    private FrameworkExecution frameworkExecution;
    private ExecutionControl executionControl;

    // Parameters
    private ActionParameterOperation expectedValue;
    private ActionParameterOperation actualValue;
    private HashMap<String, ActionParameterOperation> actionParameterOperationMap;

    // Constructors
    public EvalAssertEquals() {

    }

    public EvalAssertEquals(FrameworkExecution frameworkExecution, ExecutionControl executionControl,
                            ScriptExecution scriptExecution, ActionExecution actionExecution) {
        this.init(frameworkExecution, executionControl, scriptExecution, actionExecution);
    }

    public void init(FrameworkExecution frameworkExecution, ExecutionControl executionControl,
                     ScriptExecution scriptExecution, ActionExecution actionExecution) {
        this.setFrameworkExecution(frameworkExecution);
        this.setExecutionControl(executionControl);
        this.setActionExecution(actionExecution);
        this.setActionParameterOperationMap(new HashMap<String, ActionParameterOperation>());
    }

    public void prepare() {
        // Reset Parameters
        this.setExpectedValue(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "expected"));
        this.setActualValue(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "actual"));

        // Get Parameters
        for (ActionParameter actionParameter : this.getActionExecution().getAction().getParameters()) {
            if (actionParameter.getName().equalsIgnoreCase("expected")) {
                this.getExpectedValue().setInputValue(actionParameter.getValue());
            } else if (actionParameter.getName().equalsIgnoreCase("actual")) {
                this.getActualValue().setInputValue(actionParameter.getValue());
            }
        }

        // Create parameter list
        this.getActionParameterOperationMap().put("expected", this.getExpectedValue());
        this.getActionParameterOperationMap().put("actual", this.getActualValue());
    }

    public boolean execute() {
        try {
            String expectedValue = convertExpectedValue(getExpectedValue().getValue());
            String actualValue = convertActualValue(getActualValue().getValue());
            return compare(expectedValue, actualValue);
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));

            this.getActionExecution().getActionControl().increaseErrorCount();

            this.getActionExecution().getActionControl().logOutput("exception", e.getMessage());
            this.getActionExecution().getActionControl().logOutput("stacktrace", StackTrace.toString());

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
            this.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for actualValue",
                    actualValue.getClass()), Level.WARN);
            return actualValue.toString();
        }
    }

    private String convertExpectedValue(DataType expectedValue) {
        if (expectedValue instanceof Text) {
            return expectedValue.toString();
        } else {
            this.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for expectedValue",
                    expectedValue.getClass()), Level.WARN);
            return expectedValue.toString();
        }
    }

    // Getters and Setters
    public FrameworkExecution getFrameworkExecution() {
        return frameworkExecution;
    }

    public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
        this.frameworkExecution = frameworkExecution;
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