package io.metadew.iesi.script.operation;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.DataTypeResolver;
import io.metadew.iesi.datatypes.Text;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.configuration.type.ActionTypeParameterConfiguration;
import io.metadew.iesi.metadata.definition.ActionTypeParameter;
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
    private DataType value;
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
                this.getFrameworkExecution().getFrameworkInstance());
        this.setActionTypeParameter(
                actionTypeParameterConfiguration.getActionTypeParameter(this.getActionTypeName(), this.getName()));
    }


    private String lookupSubroutine(String input) {
        if (this.getActionTypeParameter().getSubroutine() == null
                || this.getActionTypeParameter().getSubroutine().equalsIgnoreCase(""))
            return input;
        this.setSubroutineOperation(new SubroutineOperation(this.getFrameworkExecution(), input));
        if (this.getSubroutineOperation().isValid()) {
            if (this.getSubroutineOperation().getSubroutine().getType().equalsIgnoreCase("query")) {
                return new SqlStatementSubroutine(this.getSubroutineOperation().getSubroutine()).getValue();
            } else if (this.getSubroutineOperation().getSubroutine().getType().equalsIgnoreCase("command")) {
                return new ShellCommandSubroutine(this.getSubroutineOperation().getSubroutine()).getValue();
            } else {
                return input;
            }
        } else {
            return input;
        }
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DataType getValue() {
        return value;
    }

    public void setValue(DataType value) {
        // TODO: list -> resolvement to a data type?
        this.value = value;
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
        // TODO: list resolvement to a data type
        this.inputValue = inputValue;
        String resolvedInputValue = this.getExecutionControl().getExecutionRuntime().resolveVariables(this.getActionExecution(), inputValue);
        value = new Text(inputValue);
        resolvedInputValue = lookupSubroutine(resolvedInputValue);
        this.getExecutionControl().logMessage(this.getActionExecution(),
                "action.param=" + this.getName() + ":" + resolvedInputValue, Level.DEBUG);
        resolvedInputValue = this.getExecutionControl().getExecutionRuntime().resolveConceptLookup(this.getExecutionControl(),
                resolvedInputValue, true).getValue();
        String decryptedInputValue = this.getFrameworkExecution().getFrameworkCrypto().resolve(this.getFrameworkExecution(),
                resolvedInputValue);

        // Impersonate
        if (this.getActionTypeParameter().getImpersonate().trim().equalsIgnoreCase("y")) {
            String impersonatedConnectionName = this.getExecutionControl().getExecutionRuntime()
                    .getImpersonationOperation().getImpersonatedConnection(decryptedInputValue);
            if (!impersonatedConnectionName.equalsIgnoreCase("")) {
                this.getExecutionControl().logMessage(this.getActionExecution(), "action." + this.getName()
                        + ".impersonate=" + this.getValue() + ":" + impersonatedConnectionName, Level.DEBUG);
                resolvedInputValue = impersonatedConnectionName;
            }
        }

        // Resolve to data type
        value = DataTypeResolver.resolveToDataType(resolvedInputValue, frameworkExecution.getFrameworkConfiguration().getFolderConfiguration(), executionControl.getExecutionRuntime());
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