package io.metadew.iesi.script.action.fwk;

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
import java.text.MessageFormat;
import java.util.HashMap;


public class FwkSetParameterValue {

    private ActionParameterOperation operationName;
    private ActionParameterOperation operationValue;
    private static final Logger LOGGER = LogManager.getLogger();

    // Constructors
    public FwkSetParameterValue() {

    }

    public FwkSetParameterValue(ExecutionControl executionControl, ScriptExecution scriptExecution, ActionExecution actionExecution) {
        this.init(executionControl, scriptExecution, actionExecution);
    }

    public void init(ExecutionControl executionControl, ScriptExecution scriptExecution, ActionExecution actionExecution) {
        this.setExecutionControl(executionControl);
        this.setActionExecution(actionExecution);
        this.setActionParameterOperationMap(new HashMap<String, ActionParameterOperation>());
    }

    public void prepare()  {
        // Reset Parameters
        this.setOperationName(new ActionParameterOperation(this.getExecutionControl(), this.getActionExecution(),
                this.getActionExecution().getAction().getType(), "name"));
        this.setOperationValue(new ActionParameterOperation(this.getExecutionControl(), this.getActionExecution(),
                this.getActionExecution().getAction().getType(), "value"));

        // Get Parameters
        for (ActionParameter actionParameter : this.getActionExecution().getAction().getParameters()) {
            if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("name")) {
                this.getOperationName().setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("value")) {
                this.getOperationValue().setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            }
        }

        //Create parameter list
        this.getActionParameterOperationMap().put("name", this.getOperationName());
        this.getActionParameterOperationMap().put("value", this.getOperationValue());
    }

    public boolean execute() throws InterruptedException {
        try {
            String name = convertName(getOperationName().getValue());
            String value = convertValue(getOperationValue().getValue());
            return setParameter(name, value);
        } catch (InterruptedException e) {
            throw(e);
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));

            this.getActionExecution().getActionControl().increaseErrorCount();

            this.getActionExecution().getActionControl().logOutput("exception", e.getMessage());
            this.getActionExecution().getActionControl().logOutput("stacktrace", StackTrace.toString());

            return false;
        }

    }

    private boolean setParameter(String name, String value) throws InterruptedException {
        this.getExecutionControl().getExecutionRuntime().setRuntimeVariable(actionExecution, name, value);
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