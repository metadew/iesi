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


public class FwkSetEnvironment extends ActionTypeExecution {

    private ActionParameterOperation environmentName;
    private static final Logger LOGGER = LogManager.getLogger();

    public FwkSetEnvironment(ExecutionControl executionControl, ScriptExecution scriptExecution, ActionExecution actionExecution) {
        super(executionControl, scriptExecution, actionExecution);
    }

    public void prepare() {
        // Reset Parameters
        this.setEnvironmentName(new ActionParameterOperation(this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "environment"));

        // Get Parameters
        for (ActionParameter actionParameter : this.getActionExecution().getAction().getParameters()) {
            if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("environment")) {
                this.getEnvironmentName().setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            }
        }

        //Create parameter list
        this.getActionParameterOperationMap().put("environment", this.getEnvironmentName());
    }

    protected boolean executeAction() throws InterruptedException {
        String environmentName = convertEnvironmentName(getEnvironmentName().getValue());
        this.getExecutionControl().setEnvironment(getActionExecution(), environmentName);
        this.getActionExecution().getActionControl().increaseSuccessCount();
        return true;
    }

    private String convertEnvironmentName(DataType environmentName) {
        if (environmentName instanceof Text) {
            return environmentName.toString();
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for expect environmentName",
                    environmentName.getClass()));
            return environmentName.toString();
        }
    }

    public ActionParameterOperation getEnvironmentName() {
        return environmentName;
    }

    public void setEnvironmentName(ActionParameterOperation environmentName) {
        this.environmentName = environmentName;
    }

}