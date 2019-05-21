package io.metadew.iesi.script.action;

import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.ActionParameter;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;

public class FwkSetParameterValue {

    private ActionExecution actionExecution;
    private FrameworkExecution frameworkExecution;
    private ExecutionControl executionControl;

    // Parameters
    private ActionParameterOperation operationName;
    private ActionParameterOperation operationValue;
    private HashMap<String, ActionParameterOperation> actionParameterOperationMap;

    // Constructors
    public FwkSetParameterValue() {

    }

    public FwkSetParameterValue(FrameworkExecution frameworkExecution, ExecutionControl executionControl, ScriptExecution scriptExecution, ActionExecution actionExecution) {
        this.init(frameworkExecution, executionControl, scriptExecution, actionExecution);
    }

    public void init(FrameworkExecution frameworkExecution, ExecutionControl executionControl, ScriptExecution scriptExecution, ActionExecution actionExecution) {
        this.setFrameworkExecution(frameworkExecution);
        this.setExecutionControl(executionControl);
        this.setActionExecution(actionExecution);
        this.setActionParameterOperationMap(new HashMap<String, ActionParameterOperation>());
    }

    public void prepare() {
        // Reset Parameters
        this.setOperationName(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(), this.getActionExecution(),
                this.getActionExecution().getAction().getType(), "name"));
        this.setOperationValue(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(), this.getActionExecution(),
                this.getActionExecution().getAction().getType(), "value"));

        // Get Parameters
        for (ActionParameter actionParameter : this.getActionExecution().getAction().getParameters()) {
            if (actionParameter.getName().equalsIgnoreCase("name")) {
                this.getOperationName().setInputValue(actionParameter.getValue());
            } else if (actionParameter.getName().equalsIgnoreCase("value")) {
                this.getOperationValue().setInputValue(actionParameter.getValue());
            }
        }

        //Create parameter list
        this.getActionParameterOperationMap().put("name", this.getOperationName());
        this.getActionParameterOperationMap().put("value", this.getOperationValue());
    }

    public boolean execute() {
        try {
            // Run the action
            try {
                this.getExecutionControl().getExecutionRuntime().setRuntimeVariable(this.getActionExecution(), this.getOperationName().getValue(),
                        this.getOperationValue().getValue());
                this.getActionExecution().getActionControl().increaseSuccessCount();
            } catch (Exception e) {
                throw new RuntimeException("Issue setting runtime variables: " + e, e);
            }
            return true;
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));

            this.getActionExecution().getActionControl().increaseErrorCount();

            this.getActionExecution().getActionControl().logOutput("exception", e.getMessage());
            this.getActionExecution().getActionControl().logOutput("stacktrace", StackTrace.toString());

            return false;
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

    public ActionParameterOperation getOperationName() {
        return operationName;
    }

    public void setOperationName(ActionParameterOperation operationName) {
        this.operationName = operationName;
    }

    public ActionParameterOperation getOperationValue() {
        return operationValue;
    }

    public void setOperationValue(ActionParameterOperation operationValue) {
        this.operationValue = operationValue;
    }

    public HashMap<String, ActionParameterOperation> getActionParameterOperationMap() {
        return actionParameterOperationMap;
    }

    public void setActionParameterOperationMap(HashMap<String, ActionParameterOperation> actionParameterOperationMap) {
        this.actionParameterOperationMap = actionParameterOperationMap;
    }

}