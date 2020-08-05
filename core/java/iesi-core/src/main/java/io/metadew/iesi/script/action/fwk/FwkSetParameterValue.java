package io.metadew.iesi.script.action.fwk;

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


public class FwkSetParameterValue extends ActionTypeExecution {

    private ActionParameterOperation operationName;
    private ActionParameterOperation operationValue;
    private static final Logger LOGGER = LogManager.getLogger();

    public FwkSetParameterValue(ExecutionControl executionControl, ScriptExecution scriptExecution, ActionExecution actionExecution) {
        super(executionControl, scriptExecution, actionExecution);
    }

    public void prepare() {
        // Reset Parameters
        this.setOperationName(new ActionParameterOperation(this.getExecutionControl(), this.getActionExecution(),
                this.getActionExecution().getAction().getType(), "name"));
        this.setOperationValue(new ActionParameterOperation(this.getExecutionControl(), this.getActionExecution(),
                this.getActionExecution().getAction().getType(), "value"));

        // Get Parameters
        for (ActionParameter actionParameter : this.getActionExecution().getAction().getParameters()) {
            if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("name")) {
                this.getOperationName().setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("value")) {
                this.getOperationValue().setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            }
        }

        //Create parameter list
        this.getActionParameterOperationMap().put("name", this.getOperationName());
        this.getActionParameterOperationMap().put("value", this.getOperationValue());
    }

    protected boolean executeAction() throws InterruptedException {
        String name = convertName(getOperationName().getValue());
        String value = convertValue(getOperationValue().getValue());
        this.getExecutionControl().getExecutionRuntime().setRuntimeVariable(getActionExecution(), name, value);
        this.getActionExecution().getActionControl().increaseSuccessCount();
        return true;
    }

    private String convertValue(DataType value) {
        if (value instanceof Text) {
            return value.toString();
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for value",
                    value.getClass()));
            return value.toString();
        }
    }

    private String convertName(DataType name) {
        if (name instanceof Text) {
            return name.toString();
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for name",
                    name.getClass()));
            return name.toString();
        }
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

}