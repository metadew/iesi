package io.metadew.iesi.script.action.fwk;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.script.action.ActionTypeExecution;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;


public class FwkSetParameterValue extends ActionTypeExecution {

    private static final String OPERATION_NAME_KEY = "name";
    private static final String OPERATION_VALUE_KEY = "value";
    private static final Logger LOGGER = LogManager.getLogger();

    public FwkSetParameterValue(ExecutionControl executionControl, ScriptExecution scriptExecution, ActionExecution actionExecution) {
        super(executionControl, scriptExecution, actionExecution);
    }

    public void prepare() {
    }

    protected boolean executeAction() throws InterruptedException {
        String name = convertName(getParameterResolvedValue(OPERATION_NAME_KEY));
        String value = convertValue(getParameterResolvedValue(OPERATION_VALUE_KEY));
        this.getExecutionControl().getExecutionRuntime().setRuntimeVariable(getActionExecution(), name, value);
        this.getActionExecution().getActionControl().increaseSuccessCount();
        return true;
    }

    @Override
    protected String getKeyword() {
        return "fwk.setParameterValue";
    }

    private String convertValue(DataType value) {
        return value.toString();
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
}