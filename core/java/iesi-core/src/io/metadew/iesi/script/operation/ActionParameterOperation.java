package io.metadew.iesi.script.operation;

import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.configuration.ActionTypeParameterConfiguration;
import io.metadew.iesi.metadata.definition.ActionTypeParameter;
import io.metadew.iesi.runtime.definition.LookupResult;
import io.metadew.iesi.runtime.subroutine.ShellCommandSubroutine;
import io.metadew.iesi.runtime.subroutine.SqlStatementSubroutine;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import org.apache.logging.log4j.Level;

/**
 * Manage all operations for action parameters
 *
 * @author peter.billen
 */
public class ActionParameterOperation {

    private FrameworkExecution frameworkExecution;
    private ExecutionControl executionControl;
    private ActionExecution actionExecution;
    private String actionTypeName;
    private String name;
    private String value = "";
    private String inputValue = "";

    private ActionTypeParameter actionTypeParameter;
    private SubroutineOperation subroutineOperation;

    // Constructors
    public ActionParameterOperation(FrameworkExecution frameworkExecution, ExecutionControl executionControl,
                                    ActionExecution actionExecution, String actionTypeName, String name) {
        this.setFrameworkExecution(frameworkExecution);
        this.setExecutionControl(executionControl);
        this.setActionExecution(actionExecution);
        this.setActionTypeName(actionTypeName);
        this.setName(name);
        this.lookupActionTypeParameter();
    }

    public ActionParameterOperation(FrameworkExecution frameworkExecution, ExecutionControl executionControl,
                                    String actionTypeName, String name, String value) {
        this.setFrameworkExecution(frameworkExecution);
        this.setExecutionControl(executionControl);
        this.setActionTypeName(actionTypeName);
        this.setName(name);
        this.lookupActionTypeParameter();

        this.setInputValue(value);
    }

    // Methods
    private void lookupActionTypeParameter() {
        ActionTypeParameterConfiguration actionTypeParameterConfiguration = new ActionTypeParameterConfiguration(
                this.getFrameworkExecution());
        this.setActionTypeParameter(
                actionTypeParameterConfiguration.getActionTypeParameter(this.getActionTypeName(), this.getName()));
    }

    private void lookupSubroutine() {
        if (this.getActionTypeParameter().getSubroutine() == null
                || this.getActionTypeParameter().getSubroutine().equalsIgnoreCase(""))
            return;
        this.setSubroutineOperation(new SubroutineOperation(this.getFrameworkExecution(), this.getValue()));
        if (this.getSubroutineOperation().isValid()) {
            if (this.getSubroutineOperation().getSubroutine().getType().equalsIgnoreCase("query")) {
                this.setValue(new SqlStatementSubroutine(this.getSubroutineOperation().getSubroutine()).getValue());
            } else if (this.getSubroutineOperation().getSubroutine().getType().equalsIgnoreCase("command")) {
                this.setValue(new ShellCommandSubroutine(this.getSubroutineOperation().getSubroutine()).getValue());
            }

        }
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = this.getExecutionControl().getExecutionRuntime().resolveVariables(this.getActionExecution(),
                value);
    }

    public String getActionTypeName() {
        return actionTypeName;
    }

    public void setActionTypeName(String actionTypeName) {
        this.actionTypeName = actionTypeName;
    }

    public FrameworkExecution getFrameworkExecution() {
        return frameworkExecution;
    }

    public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
        this.frameworkExecution = frameworkExecution;
    }

    public String getInputValue() {
        return inputValue;
    }

    public void setInputValue(String inputValue) {
        this.inputValue = inputValue;
        String tempValue = inputValue;

        // Lookup inside the action runtime
        tempValue = this.getActionExecution().getActionControl().getActionRuntime().resolveRuntimeVariables(tempValue);
        //tempValue = this.getExecutionControl().getExecutionRuntime().resolveVariables(tempValue);

        // TODO centralize lookup logic here (get inside the execution controls)
        this.setValue(tempValue);
        this.lookupSubroutine();

        this.getExecutionControl().logMessage(this.getActionExecution(),
                "action.param=" + this.getName() + ":" + this.getValue(), Level.DEBUG);

        // Cross concept lookup
        LookupResult lookupResult = this.getExecutionControl().getExecutionRuntime().resolveConceptLookup(this.getExecutionControl(),
                this.getValue(), true);
        this.setValue(lookupResult.getValue());

        // Resolve internal encryption
        String decryptedValue = this.getFrameworkExecution().getFrameworkCrypto().resolve(this.getFrameworkExecution(),
                this.getValue());
        this.setValue(decryptedValue);

        // Impersonate
        if (this.getActionTypeParameter().getImpersonate().trim().equalsIgnoreCase("y")) {
            String impersonatedConnectionName = this.getExecutionControl().getExecutionRuntime()
                    .getImpersonationOperation().getImpersonatedConnection(this.getValue());
            if (!impersonatedConnectionName.equalsIgnoreCase("")) {
                this.getExecutionControl().logMessage(this.getActionExecution(), "action." + this.getName()
                        + ".impersonate=" + this.getValue() + ":" + impersonatedConnectionName, Level.DEBUG);
                this.setValue(impersonatedConnectionName);
            }
        }
    }

    public ExecutionControl getExecutionControl() {
        return executionControl;
    }

    public void setExecutionControl(ExecutionControl executionControl) {
        this.executionControl = executionControl;
    }

    public ActionTypeParameter getActionTypeParameter() {
        return actionTypeParameter;
    }

    public void setActionTypeParameter(ActionTypeParameter actionTypeParameter) {
        this.actionTypeParameter = actionTypeParameter;
    }

    public SubroutineOperation getSubroutineOperation() {
        return subroutineOperation;
    }

    public void setSubroutineOperation(SubroutineOperation subroutineOperation) {
        this.subroutineOperation = subroutineOperation;
    }

    public ActionExecution getActionExecution() {
        return actionExecution;
    }

    public void setActionExecution(ActionExecution actionExecution) {
        this.actionExecution = actionExecution;
    }

}