package io.metadew.iesi.framework.crypto;

import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;

public class RedactionSource {

    private ExecutionControl executionControl;
    private ActionExecution actionExecution;
    private String redactionString;

    public RedactionSource(ExecutionControl executionControl, ActionExecution actionExecution, String redactionString) {
        this.setExecutionControl(executionControl);
        this.setActionExecution(actionExecution);
        this.setRedactionString(redactionString);
    }


    // Getters and Setters
    public String getRedactionString() {
        return redactionString;
    }

    public void setRedactionString(String redactionString) {
        this.redactionString = redactionString;
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


}
