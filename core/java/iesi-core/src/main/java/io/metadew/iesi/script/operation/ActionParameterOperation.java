package io.metadew.iesi.script.operation;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.DataTypeHandler;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.common.crypto.FrameworkCrypto;
import io.metadew.iesi.metadata.configuration.type.ActionTypeParameterConfiguration;
import io.metadew.iesi.metadata.definition.action.type.ActionTypeParameter;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ExecutionRuntime;
import org.apache.logging.log4j.Level;

import java.text.MessageFormat;

/**
 * Manage all operations for action parameters
 *
 * @author peter.billen
 */
public class ActionParameterOperation {

    private final ActionTypeParameterConfiguration actionTypeParameterConfiguration;
    private ExecutionControl executionControl;
    private ActionExecution actionExecution;
    private String name;
    private DataType value;
    private String inputValue = "";

    private ActionTypeParameter actionTypeParameter;

    // Constructors
    public ActionParameterOperation(ExecutionControl executionControl, ActionExecution actionExecution, String actionTypeName, String name) {
        actionTypeParameterConfiguration = new ActionTypeParameterConfiguration();
        this.executionControl = executionControl;
        this.actionExecution = actionExecution;
        this.name = name;
        actionTypeParameter = actionTypeParameterConfiguration.getActionTypeParameter(actionTypeName, name)
                .orElseThrow(() -> new RuntimeException(MessageFormat.format("No definition found for parameter {0} of action {1}", name, actionTypeName)));
    }

    public ActionParameterOperation(ExecutionControl executionControl,
                                    String actionTypeName, String name, String value) {
        actionTypeParameterConfiguration = new ActionTypeParameterConfiguration();
        this.executionControl = executionControl;
        this.name = name;
        actionTypeParameter = actionTypeParameterConfiguration.getActionTypeParameter(actionTypeName, name)
                .orElseThrow(() -> new RuntimeException(MessageFormat.format("No definition found for parameter {0} of action {1}", name, actionTypeName)));
        this.setInputValue(value, executionControl.getExecutionRuntime());
    }

    private String lookupSubroutine(String input) {
        if (actionTypeParameter.getSubroutine() == null || actionTypeParameter.getSubroutine().equalsIgnoreCase(""))
            return input;
        SubroutineOperation subroutineOperation = new SubroutineOperation(input);
        if (subroutineOperation.isValid()) {
            if (subroutineOperation.getSubroutine().getType().equalsIgnoreCase("query")) {
                return new SqlStatementSubroutine(subroutineOperation.getSubroutine()).getValue();
            } else if (subroutineOperation.getSubroutine().getType().equalsIgnoreCase("command")) {
                return new ShellCommandSubroutine(subroutineOperation.getSubroutine()).getValue();
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

    public DataType getValue() {
        return value;
    }

    public void setInputValue(String inputValue, ExecutionRuntime executionRuntime) {
        if (inputValue == null) inputValue = "";
        // TODO: list resolvement to a data type
        // Keep input value with orginal entry
        this.inputValue = inputValue;
        executionControl.logMessage("action.param=" + name + ":" + inputValue, Level.DEBUG);

        // Start manipulation with lookups
        // Look up inside action perimeter
        inputValue = actionExecution.getActionControl().getActionRuntime().resolveRuntimeVariables(inputValue);

        // TODO centralize lookup logic here (get inside the execution controls / runtime)
        String resolvedInputValue = executionControl.getExecutionRuntime().resolveVariables(actionExecution, inputValue);

        // TODO verify if still needed
        value = new Text(inputValue);

        resolvedInputValue = lookupSubroutine(resolvedInputValue);
        resolvedInputValue = executionControl.getExecutionRuntime().resolveConceptLookup(resolvedInputValue).getValue();

        // perform lookup again after cross concept lookup
        resolvedInputValue = executionControl.getExecutionRuntime().resolveVariables(actionExecution, resolvedInputValue);
        executionControl.logMessage("action.param.resolved=" + name + ":" + resolvedInputValue, Level.DEBUG);
        String decryptedInputValue = FrameworkCrypto.getInstance().resolve(resolvedInputValue);

        // Impersonate
        if (actionTypeParameter.isImpersonate()) {
            String impersonatedConnectionName = executionControl.getExecutionRuntime()
                    .getImpersonationOperation().getImpersonatedConnection(decryptedInputValue);
            if (!impersonatedConnectionName.equalsIgnoreCase("")) {
                executionControl.logMessage("action." + name
                        + ".impersonate=" + this.getValue() + ":" + impersonatedConnectionName, Level.DEBUG);
                resolvedInputValue = impersonatedConnectionName;
            }
        }

        // Resolve to data type
        value = DataTypeHandler.getInstance().resolve(decryptedInputValue, executionRuntime);
    }

}