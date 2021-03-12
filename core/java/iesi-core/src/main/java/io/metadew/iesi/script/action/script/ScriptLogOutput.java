package io.metadew.iesi.script.action.script;

import io.metadew.iesi.common.crypto.FrameworkCrypto;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.configuration.script.result.ScriptResultOutputConfiguration;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.metadata.definition.script.result.ScriptResultOutput;
import io.metadew.iesi.metadata.definition.script.result.key.ScriptResultOutputKey;
<<<<<<< HEAD
=======
import io.metadew.iesi.script.action.ActionTypeExecution;
>>>>>>> master
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;


@Log4j2
<<<<<<< HEAD
public class ScriptLogOutput {

    private final ScriptExecution scriptExecution;
    private ActionExecution actionExecution;
    private ExecutionControl executionControl;
=======
public class ScriptLogOutput extends ActionTypeExecution {
>>>>>>> master

    // Parameters
    private ActionParameterOperation outputName;
    private ActionParameterOperation outputValue;
    private static final Logger LOGGER = LogManager.getLogger();

    public ScriptLogOutput(ExecutionControl executionControl,
                           ScriptExecution scriptExecution, ActionExecution actionExecution) {
<<<<<<< HEAD
        this.executionControl = executionControl;
        this.scriptExecution = scriptExecution;
        this.actionExecution = actionExecution;
        this.actionParameterOperationMap = new HashMap<>();
=======
        super(executionControl, scriptExecution, actionExecution);
>>>>>>> master
    }

    public void prepare() {
        // Reset Parameters
        this.outputName = new ActionParameterOperation(getExecutionControl(), getActionExecution(), getActionExecution().getAction().getType(), "name");
        this.outputValue = new ActionParameterOperation(getExecutionControl(), getActionExecution(), getActionExecution().getAction().getType(), "value");

        // Get Parameters
        for (ActionParameter actionParameter : getActionExecution().getAction().getParameters()) {
            if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("name")) {
                outputName.setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("value")) {
                outputValue.setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            }
        }

        // Create parameter list
        getActionParameterOperationMap().put("name", outputName);
        getActionParameterOperationMap().put("value", outputValue);
    }

    protected boolean executeAction() throws InterruptedException {
        String name = convertOutputName(outputName.getValue());
        String value = convertOutputValue(outputValue.getValue());

        // Log the output in the action as well
<<<<<<< HEAD
        actionExecution.getActionControl().logOutput("output.name", name);
        actionExecution.getActionControl().logOutput("output.value", value);

        // log the output in the script
        value = FrameworkCrypto.getInstance().redact(value);
        log.info("action.output=" + name + ":" + value, Level.INFO);
        ScriptResultOutput scriptResultOutput = new ScriptResultOutput(
                new ScriptResultOutputKey(executionControl.getRunId(), executionControl.getProcessId(), name),
                scriptExecution.getScript().getMetadataKey().getScriptId(),
                value);
        ScriptResultOutputConfiguration.getInstance().insert(scriptResultOutput);

        actionExecution.getActionControl().increaseSuccessCount();
=======
        getActionExecution().getActionControl().logOutput("output.name", name);
        getActionExecution().getActionControl().logOutput("output.value", value);

        // log the output in the script
        value = FrameworkCrypto.getInstance().redact(value);
        log.info("action.output=" + name + ":" + value, Level.INFO);
        ScriptResultOutput scriptResultOutput = new ScriptResultOutput(
                new ScriptResultOutputKey(getExecutionControl().getRunId(), getExecutionControl().getProcessId(), name),
                getScriptExecution().getScript().getMetadataKey().getScriptId(),
                value);
        ScriptResultOutputConfiguration.getInstance().insert(scriptResultOutput);

        getActionExecution().getActionControl().increaseSuccessCount();
>>>>>>> master
        return true;
    }

    private String convertOutputValue(DataType outputValue) {
        return outputValue.toString();
    }

    private String convertOutputName(DataType outputName) {
        if (outputName instanceof Text) {
            return outputName.toString();
        } else {
            LOGGER.warn(MessageFormat.format(getActionExecution().getAction().getType() + " does not accept {0} as type for expect outputName",
                    outputName.getClass()));
            return outputName.toString();
        }
    }

}